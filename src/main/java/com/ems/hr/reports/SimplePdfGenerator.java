package com.ems.hr.reports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SimplePdfGenerator {

    public static byte[] generatePdf(String title, List<String> headers, List<List<String>> rows) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writePdf(title, headers, rows, bos);
        return bos.toByteArray();
    }

    private static void writePdf(String title, List<String> headers, List<List<String>> rows, OutputStream os) throws IOException {
        List<Long> offsets = new ArrayList<>();
        List<String> objects = new ArrayList<>();

        // We will construct the objects dynamically.
        // Object 1: Catalog
        // Object 2: Pages
        // Object 3: Font F1 (Regular)
        // Object 4: Font F2 (Bold)
        // Object 5+: Pages kids and Page Contents...

        // Let's plan pages. One page can fit about 35-40 rows.
        int rowsPerPage = 32;
        int totalPages = (int) Math.ceil((double) rows.size() / rowsPerPage);
        if (totalPages == 0) totalPages = 1;

        int catalogId = 1;
        int pagesTreeId = 2;
        int fontRegId = 3;
        int fontBoldId = 4;

        int currentObjId = 5;
        List<Integer> pageObjIds = new ArrayList<>();
        List<String> pageContents = new ArrayList<>();

        for (int p = 0; p < totalPages; p++) {
            int pageId = currentObjId++;
            int contentId = currentObjId++;
            pageObjIds.add(pageId);

            // Generate content stream for this page
            int startRow = p * rowsPerPage;
            int endRow = Math.min(startRow + rowsPerPage, rows.size());
            List<List<String>> pageRows = rows.subList(startRow, endRow);

            String content = generatePageContentStream(title, headers, pageRows, p + 1, totalPages);
            pageContents.add(content);
        }

        // Now compile the PDF stream and compute accurate byte offsets
        long currentOffset = 0;

        // Header
        byte[] headerBytes = "%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII);
        os.write(headerBytes);
        currentOffset += headerBytes.length;

        // Object List representation
        List<byte[]> objBytesList = new ArrayList<>();

        // Obj 1: Catalog
        objBytesList.add(formatObject(catalogId, "<< /Type /Catalog /Pages 2 0 R >>"));
        // Obj 2: Pages
        StringBuilder kids = new StringBuilder("[");
        for (int pid : pageObjIds) {
            kids.append(pid).append(" 0 R ");
        }
        kids.append("]");
        objBytesList.add(formatObject(pagesTreeId, "<< /Type /Pages /Kids " + kids.toString() + " /Count " + totalPages + " >>"));
        // Obj 3: Font Regular
        objBytesList.add(formatObject(fontRegId, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"));
        // Obj 4: Font Bold
        objBytesList.add(formatObject(fontBoldId, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>"));

        // Page Objects and Content Streams
        for (int i = 0; i < totalPages; i++) {
            int pageId = pageObjIds.get(i);
            int contentId = pageId + 1;
            String streamContent = pageContents.get(i);
            byte[] streamBytes = streamContent.getBytes(StandardCharsets.UTF_8);

            // Page Object
            objBytesList.add(formatObject(pageId, "<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /MediaBox [0 0 612 792] /Contents " + contentId + " 0 R >>"));
            
            // Content Stream Object
            String streamHeader = "<< /Length " + streamBytes.length + " >>\nstream\n";
            String streamFooter = "\nendstream";
            byte[] shBytes = streamHeader.getBytes(StandardCharsets.US_ASCII);
            byte[] sfBytes = streamFooter.getBytes(StandardCharsets.US_ASCII);

            ByteArrayOutputStream tempObj = new ByteArrayOutputStream();
            tempObj.write(String.format("%d 0 obj\n", contentId).getBytes(StandardCharsets.US_ASCII));
            tempObj.write(shBytes);
            tempObj.write(streamBytes);
            tempObj.write(sfBytes);
            tempObj.write("\nendobj\n".getBytes(StandardCharsets.US_ASCII));
            objBytesList.add(tempObj.toByteArray());
        }

        // Write all objects and record offsets
        int objIndex = 1;
        for (byte[] objBytes : objBytesList) {
            offsets.add(currentOffset);
            os.write(objBytes);
            currentOffset += objBytes.length;
        }

        // Xref Table
        long xrefOffset = currentOffset;
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n");
        int totalObjects = 4 + totalPages * 2;
        xref.append("0 ").append(totalObjects + 1).append("\n");
        xref.append("0000000000 65535 f \n");
        for (long offset : offsets) {
            xref.append(String.format("%010d 00000 n \n", offset));
        }

        byte[] xrefBytes = xref.toString().getBytes(StandardCharsets.US_ASCII);
        os.write(xrefBytes);
        currentOffset += xrefBytes.length;

        // Trailer
        String trailer = "trailer\n<< /Size " + (totalObjects + 1) + " /Root 1 0 R >>\n" +
                "startxref\n" + xrefOffset + "\n%%EOF\n";
        os.write(trailer.getBytes(StandardCharsets.US_ASCII));
        os.flush();
    }

    private static byte[] formatObject(int id, String content) throws IOException {
        String objStr = String.format("%d 0 obj\n%s\nendobj\n", id, content);
        return objStr.getBytes(StandardCharsets.UTF_8);
    }

    private static String generatePageContentStream(String title, List<String> headers, List<List<String>> rows, int pageNum, int totalPages) {
        StringBuilder sb = new StringBuilder();

        // Title Block
        sb.append("BT\n");
        sb.append("/F2 16 Tf\n"); // Bold Font 16pt
        sb.append("54 740 Td\n"); // Coordinates: X=54, Y=740
        sb.append("(" + escapePdfString(title) + ") Tj\n");
        sb.append("ET\n");

        // Subtitle (Metadata)
        sb.append("BT\n");
        sb.append("/F1 10 Tf\n"); // Regular Font 10pt
        sb.append("54 720 Td\n");
        sb.append("(Date: " + LocalDate.now() + "   |   Page " + pageNum + " of " + totalPages + ") Tj\n");
        sb.append("ET\n");

        // Table Header
        int y = 680;
        int colWidth = 504 / Math.max(1, headers.size()); // printable width = 612 - 54*2 = 504

        // Draw Header background rectangle
        sb.append("0.85 0.85 0.85 rg\n"); // Light gray
        sb.append("54 " + (y - 5) + " " + 504 + " 22 re\n");
        sb.append("f\n");

        // Header Text
        sb.append("BT\n");
        sb.append("/F2 10 Tf\n"); // Bold
        sb.append("0 g\n"); // Black text
        sb.append("54 " + y + " Td\n");

        for (int i = 0; i < headers.size(); i++) {
            String headerText = headers.get(i);
            // Escape and truncate if too long
            String displayVal = truncateToWidth(headerText, colWidth);
            sb.append("(" + escapePdfString(displayVal) + ") Tj\n");
            if (i < headers.size() - 1) {
                sb.append(colWidth + " 0 Td\n");
            }
        }
        sb.append("ET\n");

        // Draw border line below header
        sb.append("0.5 G\n"); // gray line
        sb.append("54 " + (y - 5) + " m\n");
        sb.append("558 " + (y - 5) + " l\n");
        sb.append("S\n");

        // Rows Text
        y -= 25;
        for (List<String> row : rows) {
            sb.append("BT\n");
            sb.append("/F1 9 Tf\n"); // Regular 9pt
            sb.append("54 " + y + " Td\n");

            for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                String cellVal = colIndex < row.size() ? row.get(colIndex) : "";
                if (cellVal == null) cellVal = "";
                String displayVal = truncateToWidth(cellVal, colWidth);
                sb.append("(" + escapePdfString(displayVal) + ") Tj\n");
                if (colIndex < row.size() - 1) {
                    sb.append(colWidth + " 0 Td\n");
                }
            }
            sb.append("ET\n");

            // Row divider line
            sb.append("0.9 G\n"); // very light gray line
            sb.append("54 " + (y - 4) + " m\n");
            sb.append("558 " + (y - 4) + " l\n");
            sb.append("S\n");

            y -= 18;
        }

        return sb.toString();
    }

    private static String escapePdfString(String s) {
        if (s == null) return "";
        return s.replace("(", "\\(").replace(")", "\\)").replace("\\", "\\\\");
    }

    private static String truncateToWidth(String text, int width) {
        // Simple heuristic: font size 9/10 Helvetica is about 0.5-0.6 of height in width per char
        int maxChars = width / 5;
        if (text.length() <= maxChars) {
            return text;
        }
        return text.substring(0, Math.max(3, maxChars - 3)) + "...";
    }
}
