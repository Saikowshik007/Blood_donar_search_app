package com.example.project;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter {
    private List<User> userList;
    private Context contex;
    public ListAdapter(Context context, List<User> messageList) {
      userList = messageList;
      contex=context;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    @Override
    public int getItemViewType(int position) {
        User message = userList.get(position);

        return position;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.users_layout, parent, false);
            return new userHolder(view);
        }


    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User details =userList.get(position);

        ((userHolder) holder).bind(details);
        }


    private class userHolder extends RecyclerView.ViewHolder {
        TextView name,phone,age,group;

        userHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            age = itemView.findViewById(R.id.user_age);
            phone = itemView.findViewById(R.id.user_phone);
            group = itemView.findViewById(R.id.user_group);
        }

        void bind(User message) {
           name.setText(message.getName());
           age.setText(message.getAge());
           phone.setText(message.getPhone());
           group.setText(message.getGroup());

        }
    }

}
