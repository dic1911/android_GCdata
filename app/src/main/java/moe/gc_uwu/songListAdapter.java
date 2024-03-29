package moe.gc_uwu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class songListAdapter extends ArrayAdapter<musicTemplate> implements View.OnClickListener {

    private ArrayList<musicTemplate> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView id;
        TextView title;
    }

    public songListAdapter(ArrayList<musicTemplate> data, Context context) {
        super(context, R.layout.music_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        musicTemplate dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final songListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.music_item, parent, false);
            viewHolder.id = (TextView) convertView.findViewById(R.id.ID);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            //result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (songListAdapter.ViewHolder) convertView.getTag();
            //result=convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        //lastPosition = position;

        viewHolder.id.setText(dataModel.getId());
        viewHolder.title.setText(dataModel.getTitle());
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void onClick(View view) {

    }
}
