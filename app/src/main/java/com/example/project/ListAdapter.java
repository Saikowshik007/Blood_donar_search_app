package com.example.project;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.icu.text.Transliterator;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.ImmutableList;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class ListAdapter extends RecyclerView.Adapter implements Filterable {
    private List<User> userList;
    ImmutableList<User> complete;
    private Context contex;
    Dialog mDialouge;
    TextView name,lastdonated;
    FloatingActionButton phone, message,rate;
    ImageView img2,image;
    String num, pname, pnum;
    String s;
    List<User> currentuser;
    private int lastPosition = -1;

    public ListAdapter(Context context, List<User> messageList, List<User> currentUser) {
        userList = messageList;
        contex = context;
        currentuser = currentUser;
    }

    public void dup(List<User> dup) {
        dup.remove(currentuser);
        complete = ImmutableList.copyOf(dup);
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

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final User details = userList.get(position);
            ((userHolder) holder).bind(details);
            mDialouge = new Dialog(contex);
            mDialouge.setContentView(R.layout.fragment);
        mDialouge.getWindow().getAttributes().windowAnimations=R.anim.zoomout;
        image=holder.itemView.findViewById(R.id.imageView10);
        image.setImageResource(details.getAvailability().contains("0")?R.drawable.rounded:R.drawable.rounded_green);
            name = mDialouge.findViewById(R.id.textView11);
            phone = mDialouge.findViewById(R.id.imageButton2);
            img2 = mDialouge.findViewById(R.id.imageView2);
            message = mDialouge.findViewById(R.id.imageButton4);
        rate = mDialouge.findViewById(R.id.imageButton3);
            lastdonated = mDialouge.findViewById(R.id.textView12);
            pname = currentuser.get(0).getName();
          pnum = currentuser.get(0).getPhone();
            Toast.makeText(contex, "Unable to create user", Toast.LENGTH_SHORT);
            setAnimation(holder.itemView, position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    name.setText(details.getName());
                    num = details.getPhone();
                    lastdonated.setText(details.getDate());
                    switch (details.getGroup()) {
                        case "O+":
                            img2.setImageResource(R.drawable.oplus);
                            break;
                        case "O-":
                            img2.setImageResource(R.drawable.ominus);
                            break;
                        case "A+":
                            img2.setImageResource(R.drawable.aplus);
                            break;
                        case "A-":
                            img2.setImageResource(R.drawable.aminus);
                            break;
                        case "B+":
                            img2.setImageResource(R.drawable.bplus);
                            break;
                        case "B-":
                            img2.setImageResource(R.drawable.bminus);
                            break;
                    }

                    mDialouge.show();
                }
            });
            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(contex, "Text", Toast.LENGTH_LONG).show();
                    if (num.length() >= 10) {
                        Uri u = Uri.parse("tel:" + num);
                        Intent i = new Intent(Intent.ACTION_DIAL, u);
                        contex.startActivity(i);
                    }
                }
            });

            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contex.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", num, null)).putExtra("sms_body", "Dear donor," +
                            "\n" + "We require an emergency blood donation at  " + details.getLocation() + " for the user " + pname + " having phone number: " + pnum + " \n" +
                            " Kindly respond immediately."));
                }
            });
            rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(contex,"Feature coming soon...",Toast.LENGTH_SHORT).show();
                }
            });


    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredlist = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredlist.addAll(complete);
            } else {
                String filterpattern = constraint.toString().toLowerCase().trim();
                for (User item : complete) {
                    if (item.getGroup().toLowerCase().equals(filterpattern)) {
                        filteredlist.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredlist;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            userList.addAll((Collection<? extends User>) results.values);
            notifyDataSetChanged();

        }
    };

    public class userHolder extends RecyclerView.ViewHolder {
        TextView name, phone, age,distance;
        ImageView sample;
        public userHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            age = itemView.findViewById(R.id.user_age);
            sample = itemView.findViewById(R.id.textView13);
            distance=itemView.findViewById(R.id.distance);
        }

        void bind(User message) {
            name.setText(message.getName());
            age.setText(message.getAge());
            distance.setText(Double.toString(haversine(Double.parseDouble(currentuser.get(0).lat),Double.parseDouble(currentuser.get(0).lon),Double.parseDouble(message.getLat()),Double.parseDouble(message.getLon())))+"KMS");
            if (message.getGroup().equals("O+")) {
                sample.setImageResource(R.drawable.oplus);
            } else if (message.getGroup().contains("O-")) {
                sample.setImageResource(R.drawable.oplus);
            } else if (message.getGroup().contains("A+")) {
                sample.setImageResource(R.drawable.aplus);
            } else if (message.getGroup().contains("B+")) {
                sample.setImageResource(R.drawable.oplus);
            } else if (message.getGroup().contains("B-")) {
                sample.setImageResource(R.drawable.bminus);
            } else if (message.getGroup().contains("AB-")) {
                sample.setImageResource(R.drawable.abminus);
            }
            else if (message.getGroup().contains("AB+")) {
                sample.setImageResource(R.drawable.abminus);
            }
                else if (message.getGroup().contains("A-")) {
                    sample.setImageResource(R.drawable.aminus);
            }

        }
    }

    public void clear() {
        userList.clear();
    }

    private void setAnimation(View viewToAnimate, int position) {

       Animation animation = AnimationUtils.loadAnimation(contex, R.anim.bounce);
            viewToAnimate.startAnimation(animation);
    }
    static double haversine(double lat1, double lon1,
                            double lat2, double lon2)
    {
       DecimalFormat df2 = new DecimalFormat("#.##");
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        double distance= rad * c;
        return Double.parseDouble(df2.format(distance));
    }
    public void msort(){
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Double.toString(haversine(Double.parseDouble(currentuser.get(0).lat),Double.parseDouble(currentuser.get(0).lon),Double.parseDouble(o1.getLat()),Double.parseDouble(o1.getLon())))
                        .compareTo(Double.toString(haversine(Double.parseDouble(currentuser.get(0).lat),Double.parseDouble(currentuser.get(0).lon),Double.parseDouble(o2.getLat()),Double.parseDouble(o2.getLon()))));

            }
        });

    }
}
