package com.edu.tutorialaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.balysv.materialripple.MaterialRippleLayout;
import com.edu.tutorialaction.entity.Reserve;
import com.edu.tutorialaction.network.ReserveModel;
import com.edu.tutorialaction.network.RxLoaderActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observer;


public class ReservesAdapter extends BaseAdapter {

    private final ArrayList<Reserve> reserves;
    private final Context context;

    public ReservesAdapter(Context context) {
        this.context = context;
        this.reserves = new ArrayList<>();
    }

    public void addReserves(List<Reserve> reserves) {
        this.reserves.addAll(reserves);

        notifyDataSetChanged();
    }

    public void addReserve(Reserve reserve) {
        reserves.add(reserve);
        notifyDataSetChanged();
    }

    public boolean removeReserve(int reserveID) {
        boolean remove = false;
        Reserve reserve;
        Iterator<Reserve> reserveIterator = reserves.iterator();

        do {
            reserve = reserveIterator.next();
            if(reserve.getReserveid() == reserveID) {
                remove = true;
            }
        } while (reserveIterator.hasNext() && !remove);

        if(remove) {
            reserves.remove(reserve);
            notifyDataSetChanged();
        }

        return remove;
    }

    public void clearReserves() {
        reserves.clear();
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return reserves.size();
    }

    @Override
    public Reserve getItem(int position) {
        return reserves.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_reserve, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.reserve_fullname)).setText(reserves.get(position).getUserFullname());
        ((TextView) convertView.findViewById(R.id.reserve_course_name)).setText(reserves.get(position).getCourseName());
        ((TextView) convertView.findViewById(R.id.reserve_email)).setText(reserves.get(position).getEmail());
        ((TextView) convertView.findViewById(R.id.reserve_date)).setText(reserves.get(position).getDate() + " | " + reserves.get(position).getHour());


        // More info layout
        MaterialRippleLayout moreInfoButton = (MaterialRippleLayout) convertView.findViewById(R.id.reserve_moreInfoButton);

        // Company text + description
        final View moreInfoLayout = convertView.findViewById(R.id.reserve_moreInfoLayout);
        TextView tutorshipType = (TextView) convertView.findViewById(R.id.tutorship_type);
        TextView description = (TextView) convertView.findViewById(R.id.reserve_motive);
        String tutorshipTypeText = "<b>Tipo de tutoría: </b>" + (reserves.get(position).getTutorshipType() == 0 ? "Docente" : "Académica");
        String motiveText = "<b>Motivo: </b>" + reserves.get(position).getMotive();
        tutorshipType.setText(Html.fromHtml(tutorshipTypeText));
        description.setText(Html.fromHtml(motiveText));

        // More info button
        final TextView moreInfoText = (TextView) convertView.findViewById(R.id.text);
        moreInfoText.setText("Más información");
        moreInfoText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_expand, 0, 0, 0);

        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show/Hide layout (TODO: Animated)
                boolean wasExpanded = moreInfoLayout.getVisibility() == View.VISIBLE;
                moreInfoLayout.setVisibility(wasExpanded ? View.GONE : View.VISIBLE);

                // Adjust compound button
                int icon = wasExpanded ? R.drawable.ic_arrow_expand : R.drawable.ic_arrow_collapse;
                moreInfoText.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
                moreInfoText.setText(wasExpanded ? "Más información" : "Menos información");
            }
        });



        // More info button
        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.delete_button);
        final Context context = convertView.getContext();
        final int reserveID = reserves.get(position).getReserveid();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                System.out.println("Removing reserve.");

                                RxLoaderActivity<Object> loader = new RxLoaderActivity<Object>() {
                                    @Override
                                    public void onNext(Object response) {

                                    }
                                };

                                loader.addSubscription(ReserveModel.INSTANCE.removeReserve(new Observer<Object>() {
                                    @Override
                                    public void onCompleted() {
                                        System.out.println("Removing reserve COMPLETED.");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        System.out.println("Removing reserve ERROR.");
                                    }

                                    @Override
                                    public void onNext(Object o) {
                                        System.out.println("Removing reserve NEXT.");
                                        removeReserve(reserveID);
                                    }
                                }, context, reserveID));

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("¿Quieres cancelar la reserva?").setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });


        return convertView;
    }
}
