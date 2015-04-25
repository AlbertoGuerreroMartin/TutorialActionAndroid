package com.edu.tutorialaction;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.edu.tutorialaction.entity.Tutorship;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompletedTutorshipsAdapter extends BaseAdapter {

    private final ArrayList<Tutorship> tutorships;
    private final Context context;

    public CompletedTutorshipsAdapter(Context context) {
        this.context = context;
        this.tutorships = new ArrayList<>();
    }

    public void addTutorships(List<Tutorship> tutorships) {
        this.tutorships.addAll(tutorships);

        notifyDataSetChanged();
    }

    public void addTutorship(Tutorship tutorship) {
        tutorships.add(tutorship);
        notifyDataSetChanged();
    }

    public boolean removeTutorship(int tutorshipID) {
        boolean remove = false;
        Tutorship tutorship;
        Iterator<Tutorship> tutorshipIterator = tutorships.iterator();

        do {
            tutorship = tutorshipIterator.next();
            if(tutorship.getTutorshipid() == tutorshipID) {
                remove = true;
            }
        } while (tutorshipIterator.hasNext() && !remove);

        if(remove) {
            tutorships.remove(tutorship);
            notifyDataSetChanged();
        }

        return remove;
    }

    public void clearTutorships() {
        tutorships.clear();
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return tutorships.size();
    }

    @Override
    public Tutorship getItem(int position) {
        return tutorships.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_completed_tutorship, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.completed_tutorship_fullname)).setText(tutorships.get(position).getUserFullname());
        ((TextView) convertView.findViewById(R.id.completed_tutorship_course_name)).setText(tutorships.get(position).getCourseName());
        ((TextView) convertView.findViewById(R.id.completed_tutorship_date)).setText(tutorships.get(position).getDate() + " | " + tutorships.get(position).getHour() + " (" + tutorships.get(position).getDuration() + " min.)");


        // More info layout
        MaterialRippleLayout moreInfoButton = (MaterialRippleLayout) convertView.findViewById(R.id.completed_tutorship_moreInfoButton);

        // Company text + description
        final View moreInfoLayout = convertView.findViewById(R.id.completed_tutorship_moreInfoLayout);
        TextView description = (TextView) convertView.findViewById(R.id.completed_tutorship_motive);
        String motiveText = "<b>Motivo: </b>" + tutorships.get(position).getMotive();
        description.setText(Html.fromHtml(motiveText));

        // More info button
        final TextView moreInfoText = (TextView) convertView.findViewById(R.id.text);
        moreInfoText.setText("Más información");
        moreInfoText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_expand , 0, 0, 0);

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

        return convertView;
    }
}
