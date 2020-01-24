package com.example.gmagic;

import java.util.ArrayList;

public class WholeContact {


    WholeContact(){
        subcontacts = new ArrayList<>();
    }

    String name;
    String num;
    ArrayList<Contact> subcontacts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public ArrayList<Contact> getSubcontacts() {
        return subcontacts;
    }

    public void setSubcontacts(ArrayList<Contact> subcontacts) {
        this.subcontacts = subcontacts;
    }
}
