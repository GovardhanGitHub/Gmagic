package com.example.gmagic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {
     List<Contact> contacts;

     public ContactAdapter(List<Contact> contacts) {
         this.contacts = contacts;
     }

     public ContactAdapter(Set<Contact> contacts){
         this.contacts = new ArrayList<>(contacts);
     }

     @NonNull
     @Override
     public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

         View itemView = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.contact_list_row, parent, false);

         return new MyViewHolder(itemView);

     }

     @Override
     public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

         Contact contact = contacts.get(position);
         holder.name.setText(contact.getName());
         holder.num.setText(contact.getNum());

     }

     @Override
     public int getItemCount() {
         return contacts.size();
     }

     public class MyViewHolder extends  RecyclerView.ViewHolder {
         public TextView name,  num;

         public MyViewHolder(View view) {
             super(view);
             name = (TextView) view.findViewById(R.id.namelabel);
             num = (TextView) view.findViewById(R.id.numlabel);

         }
     }
 }




   /* private List<Contact> moviesList;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
        }
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact movie = moviesList.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());
        holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

}
*/

