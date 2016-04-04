package aexp.elistcbox;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import java.util.ArrayList;

public class ColorAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<Color>> colors;
    private LayoutInflater inflater;

    public ColorAdapter(Context context, 
                        ArrayList<String> groups,
						ArrayList<ArrayList<Color>> colors ) { 
        this.context = context;
		this.groups = groups;
        this.colors = colors;
        inflater = LayoutInflater.from( context );
    }

    public Object getChild(int groupPosition, int childPosition) {
        return colors.get( groupPosition ).get( childPosition );
    }

    public long getChildId(int groupPosition, int childPosition) {
        return (long)( groupPosition*1024+childPosition );  // Max 1024 children per group
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.child_row, parent, false); 
        Color c = (Color)getChild( groupPosition, childPosition );
		TextView color = (TextView)v.findViewById( R.id.childname );
		if( color != null )
			color.setText( c.getColor() );
		TextView rgb = (TextView)v.findViewById( R.id.rgb );
		if( rgb != null )
			rgb.setText( c.getRgb() );
		CheckBox cb = (CheckBox)v.findViewById( R.id.check1 );
        cb.setChecked( c.getState() );
        return v;
    }

    public int getChildrenCount(int groupPosition) {
        return colors.get( groupPosition ).size();
    }

    public Object getGroup(int groupPosition) {
        return groups.get( groupPosition );        
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return (long)( groupPosition*1024 );  // To be consistent with getChildId
    } 

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.group_row, parent, false); 
        String gt = (String)getGroup( groupPosition );
		TextView colorGroup = (TextView)v.findViewById( R.id.childname );
		if( gt != null )
			colorGroup.setText( gt );
        return v;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    } 

    public void onGroupCollapsed (int groupPosition) {} 
    public void onGroupExpanded(int groupPosition) {}


}
