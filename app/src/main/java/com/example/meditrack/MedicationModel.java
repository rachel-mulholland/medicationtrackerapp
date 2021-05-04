package com.example.meditrack;
/**
 * MedicationModel.java
 * Class that contains 7 variables needed for data insertion with getters and setteres for each
 */

public class MedicationModel {

    private String ID, medName, medDosage,pktQuantity,qtyToTake, time1,time2;

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    public String getMedName()
    {
        return medName;
    }

    public String getMedDosage()
    {
        return medDosage;
    }

    public void setMedName(String medName)
    {
        this.medName = medName;
    }

    public void setMedDosage(String medDosage)
    {
        this.medDosage = medDosage;
    }

    public String getTime1()
    {
        return time1;
    }

    public void setTime1(String time1)
    {
        this.time1 = time1;
    }

    public String getTime2()
    {
        return time2;
    }

    public void setTime2(String time2)
    {
        this.time2 = time2;
    }

    public String getPktQuantity()
    {
        return pktQuantity;
    }

    public void setPktQuantity(String pktQuantity)
    {
        this.pktQuantity = pktQuantity;
    }

    public String getQtyToTake()
    {
        return qtyToTake;
    }

    public void setQtyToTake(String qtyToTake)
    {
        this.qtyToTake = qtyToTake;
    }
}
