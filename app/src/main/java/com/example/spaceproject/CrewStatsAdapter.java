package com.example.spaceproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CrewStatsAdapter extends RecyclerView.Adapter<CrewStatsAdapter.ViewHolder> {

    private final List<CrewMember> crewList;

    public CrewStatsAdapter(List<CrewMember> crewList) {
        this.crewList = crewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crew_stats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CrewMember member = crewList.get(position);
        holder.tvCrewNameRole.setText(member.name + " (" + member.role + ")");
        holder.tvMissions.setText("🎯 Missions: " + member.getMissionsParticipated());
        holder.tvWins.setText("🏆 Wins: " + member.getMissionsWon());
        holder.tvLosses.setText("💀 Losses: " + member.getMissionsLost());
        holder.tvTraining.setText("🏋️ Training Sessions: " + member.getTrainingSessions());

        int resId = holder.itemView.getContext().getResources().getIdentifier(
                member.getPortraitDrawable(), "drawable", holder.itemView.getContext().getPackageName());
        if (resId != 0) {
            holder.ivCrewIcon.setImageResource(resId);
        }
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCrewIcon;
        TextView tvCrewNameRole, tvMissions, tvWins, tvLosses, tvTraining;

        ViewHolder(View itemView) {
            super(itemView);
            ivCrewIcon = itemView.findViewById(R.id.ivCrewIcon);
            tvCrewNameRole = itemView.findViewById(R.id.tvCrewNameRole);
            tvMissions = itemView.findViewById(R.id.tvMissions);
            tvWins = itemView.findViewById(R.id.tvWins);
            tvLosses = itemView.findViewById(R.id.tvLosses);
            tvTraining = itemView.findViewById(R.id.tvTraining);
        }
    }
}
