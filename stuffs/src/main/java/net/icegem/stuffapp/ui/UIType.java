package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Text;
import net.icegem.stuffapp.data.Type;
import net.icegem.stuffapp.database.DBConnection;
import net.icegem.stuffapp.database.DBType;

import java.util.List;

/**
 * Created by mikael.korpela on 3.6.2015.
 */
public class UIType {
    public static class Manager extends net.icegem.stuffapp.ui.RowAdapter<Type> {
        private DBConnection connection;
        private Activity activity;

        public Manager(Activity activity, DBConnection connection ) {
            super(activity, R.layout.row_type_edit, DBType.list(connection));
            this.connection = connection;
            this.activity = activity;
        }

        private int findLocation( Type type ) {
            for( int i = 0 ; i < original.size() ; ++i ) {
                final Type comp = original.get(i);
                if( comp.getId() == type.getId() ) {
                    return i;
                }
            }
            return -1;
        }

        public void add( Type type ) {
            if( type == null ) {
                return;
            }
            type = DBType.save(connection , type );

            int location = findLocation(type);
            if( location < 0) {
                original.add(type);
            }

            notifyDataSetChanged();
        }

        public void remove( Type type ) {
            if( type == null ) {
                return;
            }
            int location = findLocation(type);
            if( location > 0) {
                original.remove(location);
            }
            DBType.delete(connection, type);

            notifyDataSetChanged();
        }

        public void update() {
            setup(DBType.list(connection));
            notifyDataSetChanged();
        }

        private class Tag {
            public EditText name;
            public ImageButton remove;
            public Type type;
            public int index;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            final Tag tag;

            if (convertView == null) {
                view = inflater.inflate(resourceId, parent, false);
                tag = new Tag();
                view.setTag(tag);

                tag.name = (EditText) view.findViewById(R.id.name);
                tag.remove = (ImageButton) view.findViewById(R.id.remove);

                final Activity activity = this.activity;
                final Manager manager = this;
                tag.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.question(
                                activity,
                                activity.getString(R.string.type_remove_topic),
                                activity.getString(R.string.type_remove_message),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        manager.remove(tag.type);
                                        tag.type = null;
                                    }
                                },
                                null);
                    }
                });

                tag.name.setOnClickListener(new AdapterView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, TextEditActivity.class);
                        intent.putExtra(Text.class.getName() , tag.type.getName());
                        intent.putExtra(Text.TARGET , Type.class.getName() );
                        activity.startActivityForResult(intent, 0);
                    }
                });

            } else {
                view = convertView;
                tag = (Tag) view.getTag();
            }

            Type type = (Type)getItem(position);

            tag.index = position;
            tag.type = type;
            tag.name.setText(type.getName().toString());

            return view;
        }
    }
}
