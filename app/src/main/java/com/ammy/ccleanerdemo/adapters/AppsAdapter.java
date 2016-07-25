package com.ammy.ccleanerdemo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ammy.ccleanerdemo.R;
import com.ammy.ccleanerdemo.pojo.App;

import java.util.List;

/**
 * Created by amandeepsingh on 22/7/16.
 */
public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppsHolder> {

    private List<App> mApps;

    public AppsAdapter(List<App> apps) {
        mApps = apps;
    }

    @Override
    public AppsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.apps_item, parent, false);

        return new AppsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppsHolder holder, int position) {

        holder.data.setText(mApps.get(position).getEnable()+"");
        holder.name.setText(mApps.get(position).getName());
        holder.packageName.setText(mApps.get(position).getPackageName());
        holder.version.setText(mApps.get(position).getVersionName());
        holder.apk.setText(mApps.get(position).getApkSize());
        holder.icon.setImageDrawable(mApps.get(position).getIcon());

    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    class AppsHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView version, packageName, name, data, apk;

        public AppsHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            version = (TextView) itemView.findViewById(R.id.tv_version);
            packageName = (TextView) itemView.findViewById(R.id.tv_package);
            name = (TextView) itemView.findViewById(R.id.tv_appname);
            data = (TextView) itemView.findViewById(R.id.tv_data_size);
            apk = (TextView) itemView.findViewById(R.id.tv_apk_size);
        }
    }
}
