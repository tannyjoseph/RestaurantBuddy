package com.g.apitask.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.g.apitask.Classes.MyList;
import com.g.apitask.R;
import com.g.apitask.Activities.RestaurantProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {

    List<MyList> lists;
    Context context;

    public MyListAdapter(List<MyList> lists, Context context) {

        // generate constructors to initialise the List and Context objects

        this.lists = lists;
        this.context = context;


    }

    @NonNull
    @Override
    public MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListAdapter.ViewHolder holder, final int position) {
        final MyList myList = lists.get(position);

        holder.name.setText(myList.getName());
        holder.address.setText(myList.getAddress());
        holder.rating.setText(myList.getRating());

        if (!myList.getPhoto().equals("")) {
            Picasso.with(context)
                    .load(myList.getPhoto())
                    .into(holder.avatar_url);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyList myList1 = lists.get(position);

                Intent skipIntent = new Intent(v.getContext(), RestaurantProfile.class);
                skipIntent.putExtra("KEY_NAME", myList1.getName());
                skipIntent.putExtra("Key_address", myList1.getAddress());
                skipIntent.putExtra("id", myList1.getId());
                skipIntent.putExtra("rating", myList1.getRating());

                v.getContext().startActivity(skipIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, address, rating;
        public ImageView avatar_url;
        public CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            avatar_url = (ImageView) itemView.findViewById(R.id.main_image);
            address = itemView.findViewById(R.id.address);
            rating = itemView.findViewById(R.id.rating);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
