/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shishkoam.spendyourfreetime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import shishkoam.spendyourfreetime.auth.AuthUiActivity;


public class ChooserActivity extends AppCompatActivity {

    private static final Class[] CLASSES = new Class[]{
            ChatActivity.class,
            AuthUiActivity.class,
    };

    private static final int[] DESCRIPTION_NAMES = new int[] {
            R.string.name_chat,
            R.string.name_auth_ui
    };

    private static final int[] DESCRIPTION_IDS = new int[] {
            R.string.desc_chat,
            R.string.desc_auth_ui
    };

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        mListView = (ListView) findViewById(R.id.list_view) ;
        mListView.setAdapter(new MyArrayAdapter(
                this,
                android.R.layout.simple_list_item_2,
                CLASSES));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });
    }

    public void onListItemClick(int position) {
        Class clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
    }

    public static class MyArrayAdapter extends ArrayAdapter<Class> {

        private Context mContext;
        private Class[] mClasses;

        public MyArrayAdapter(Context context, int resource, Class[] objects) {
            super(context, resource, objects);

            mContext = context;
            mClasses = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }

            ((TextView) view.findViewById(android.R.id.text1)).setText(DESCRIPTION_NAMES[position]);
            ((TextView) view.findViewById(android.R.id.text2)).setText(DESCRIPTION_IDS[position]);

            return view;
        }
    }
}
