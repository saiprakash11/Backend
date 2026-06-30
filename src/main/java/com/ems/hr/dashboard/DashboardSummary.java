package com.ems.hr.dashboard;

// ─────────────────────────────────────────────
// MODEL
// ─────────────────────────────────────────────
public class DashboardSummary {

    private int totalEmployees;
    private int presentToday;
    private int onLeave;
    private int newJoiners;      // joined in last 30 days

    public DashboardSummary(int totalEmployees, int presentToday,
                            int onLeave, int newJoiners) {
        this.totalEmployees = totalEmployees;
        this.presentToday   = presentToday;
        this.onLeave        = onLeave;
        this.newJoiners     = newJoiners;
    }

    public int getTotalEmployees() { return totalEmployees; }
    public int getPresentToday()   { return presentToday; }
    public int getOnLeave()        { return onLeave; }
    public int getNewJoiners()     { return newJoiners; }
}
