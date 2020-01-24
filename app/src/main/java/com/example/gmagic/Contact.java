package com.example.gmagic;

import java.io.Serializable;
class Contact  implements Serializable {


    String name;
    String num;


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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        if (!num.equals(contact.num)) return false;
        return name.equals(contact.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + num.hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", num='" + num + '\'' +
                '}';
    }
}


