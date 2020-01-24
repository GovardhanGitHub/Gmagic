package com.example.gmagic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class WholeContactAdapter extends RecyclerView.Adapter<WholeContactAdapter.MyViewHolder> {

    List<WholeContact> wholeContacts;

    WholeContactAdapter(List<WholeContact> wholeContacts)
    {
        this.wholeContacts = wholeContacts;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.whole_contact_list_row, parent, false);

        return new WholeContactAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        WholeContact wholeContact = wholeContacts.get(position);
        Contact contact = wholeContact.getSubcontacts().get(0);

        holder.masterName.setText(wholeContact.getName());
        holder.masterNum.setText(wholeContact.getNum());

        holder.childname.setText(contact.getName());
        holder.childnum.setText(contact.getNum());
    }

    @Override
    public int getItemCount() {
        return wholeContacts.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        public TextView masterName,  masterNum,childname,childnum;

        public MyViewHolder(View view) {
            super(view);
            masterName = (TextView) view.findViewById(R.id.masternamelabel);
            masterNum = (TextView) view.findViewById(R.id.masternumlabel);
            childname = (TextView) view.findViewById(R.id.childnamelabel);
            childnum = (TextView) view.findViewById(R.id.childnumlabel);




        }
    }
}
