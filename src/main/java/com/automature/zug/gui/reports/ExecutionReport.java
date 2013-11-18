package com.automature.zug.gui.reports;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: dipu
 * Date: 9/25/13
 * Time: 8:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionReport {

    private EntityType entityType;
    private String entityName;
    private Date starttime;
    private Date endtime = null;
    private Outcome outcome;
    private String footprint = null;
    private String args = null;

    public ExecutionReport(EntityType entityType, String entityName) {
        this.entityType = entityType;
        this.entityName = entityName;
        starttime = new Date();
        outcome = Outcome.INPROGRESS;
    }

    public ExecutionReport(EntityType entityType, String entityName, String args) {
        this.entityType = entityType;
        this.entityName = entityName;
        this.args = args;
        starttime = new Date();
        outcome = Outcome.INPROGRESS;
    }


    public void reportFailure(String footprint){
        endtime = new Date();
        outcome = Outcome.FAILED;
        this.footprint = footprint;
    }

    public void reportSuccess() {
        endtime = new Date();
        outcome = Outcome.COMPLETED;
    }

    public String getSummary() {
        return  null;
    }

}
