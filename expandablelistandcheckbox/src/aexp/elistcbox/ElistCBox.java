package aexp.elistcbox;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;
import android.util.Log;

public class ElistCBox extends ExpandableListActivity
{
    private static final String LOG_TAG = "ElistCBox2";
    private ColorAdapter expListAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        ArrayList<String> groupNames = new ArrayList<String>();
        groupNames.add( "grey" );
	    groupNames.add( "blue" );
	    groupNames.add( "yellow" );
	    groupNames.add( "red" );
        ArrayList<ArrayList<Color>> colors = new ArrayList<ArrayList<Color>>(); 
        ArrayList<Color> color = new ArrayList<Color>();
        color.add( new Color( "lightgrey","#D3D3D3", false ) ); 
		color.add( new Color( "dimgray","#696969", true ) ); 
		color.add( new Color( "sgi gray 92","#EAEAEA", false ) );
        colors.add( color );
        color = new ArrayList<Color>();
		color.add( new Color( "dodgerblue 2","#1C86EE",false ) );
		color.add( new Color(  "steelblue 2","#5CACEE",false ) ); 
		color.add( new Color( "powderblue","#B0E0E6", true ) );
        colors.add( color );
        color = new ArrayList<Color>();
		color.add( new Color( "yellow 1","#FFFF00",true ) );
		color.add( new Color( "gold 1","#FFD700",false ) ); 
		color.add( new Color( "darkgoldenrod 1","#FFB90F", true ) );
        colors.add( color );
        color = new ArrayList<Color>();
		color.add( new Color( "indianred 1","#FF6A6A",true ) );
		color.add( new Color( "firebrick 1","#FF3030",false ) ); 
		color.add( new Color( "maroon","#800000", false ) );
        colors.add( color );

		expListAdapter = new ColorAdapter( this,groupNames, colors );
		setListAdapter( expListAdapter );
    }

    public void onContentChanged  () {
        super.onContentChanged();
        Log.d( LOG_TAG, "onContentChanged" );
    }

    public boolean onChildClick(
            ExpandableListView parent, 
            View v, 
            int groupPosition,
            int childPosition,
            long id) {
        Log.d( LOG_TAG, "onChildClick: "+childPosition );
        CheckBox cb = (CheckBox)v.findViewById( R.id.check1 );
        if( cb != null )
            cb.toggle();
        return false;
    }
}
