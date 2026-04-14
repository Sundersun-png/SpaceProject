package com.example.spaceproject;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {

    private final List<CrewMember> crewList;

    public CrewAdapter(List<CrewMember> crewList) {
        this.crewList = crewList;
    }

    public static class CrewViewHolder extends RecyclerView.ViewHolder {
        View colorDot;
        TextView tvName;
        TextView tvSpec;
        TextView tvStats;
        CardView card;

        public CrewViewHolder(View itemView) {
            super(itemView);
            colorDot = itemView.findViewById(R.id.viewColorDot);
            tvName   = itemView.findViewById(R.id.tvCrewName);
            tvSpec   = itemView.findViewById(R.id.tvSpecialization);
            tvStats  = itemView.findViewById(R.id.tvStats);
            card     = itemView.findViewById(R.id.cardCrewItem);
        }
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew_member, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CrewMember crew = crewList.get(position);

        // Draw circular colored dot for specialization
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(crew.getSpecializationColor());
        holder.colorDot.setBackground(circle);

        holder.tvName.setText(crew.getName());
        holder.tvSpec.setText(crew.getSpecialization());
        holder.tvStats.setText("XP:" + crew.getExperience() + "   Skill: " + crew.getSkill());

        // Highlight card if selected
        if (crew.isSelected()) {
            holder.card.setCardBackgroundColor(Color.parseColor("#66FFFFFF"));

        } else {
            holder.card.setCardBackgroundColor(Color.parseColor("#33FFFFFF"));

        }

        // Tap to select/deselect
        holder.card.setOnClickListener(v -> {
            crew.setSelected(!crew.isSelected());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }
}