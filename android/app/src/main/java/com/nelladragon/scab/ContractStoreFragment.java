// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nelladragon.scab.store.StoreContract;
import com.nelladragon.scab.store.StoreMain;

import java.util.List;


/**
 * Fragnment for displaying the app store.
 *
 * Currently, this is just a list. In future, this should be:
 *
 * Featured App:
 * List of categories.
 *
 */
public class ContractStoreFragment extends Fragment {

    /**
     * Adapter class to allow data to be transferred from the list and the activity.
     */
    private class ContractListAdapter extends BaseAdapter {
        private class ViewHolder {
            protected ImageView image;
            protected TextView name;
            protected TextView description;
        }


        private List<StoreContract> storeContracts;
        private LayoutInflater inflater;
        private Context context;

        public ContractListAdapter(Context context, List<StoreContract> profileItems) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.storeContracts = profileItems;
        }


        public int getCount() {
            return this.storeContracts.size();
        }

        // Get the item associated with the position in the dataset.
        public Object getItem(int position) {
            return this.storeContracts.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ContractStoreFragment.ContractListAdapter.ViewHolder holder;

            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.list_item_contractstore, parent, false);

                holder = new ContractStoreFragment.ContractListAdapter.ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.textViewContractName);
                holder.image = (ImageView) convertView.findViewById(R.id.contractImage);
                holder.description = (TextView) convertView.findViewById(R.id.textViewContractDescription);

                convertView.setTag(holder);
            } else {
                holder = (ContractStoreFragment.ContractListAdapter.ViewHolder) convertView.getTag();
            }

            StoreContract item = this.storeContracts.get(position);
            holder.name.setText(item.getName());
            holder.description.setText(item.getStoreListDescription());
            holder.image.setImageDrawable(item.getStoreListImage());

            return convertView;
        }
    }



    ListView listView;
    ContractStoreFragment.ContractListAdapter adapter;

//    List<UserProfile> storeContracts;
  //  UserController controller;

    List<StoreContract> contracts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contractstore, container, false);


        Context appContext = this.getContext().getApplicationContext();

        StoreMain controller = StoreMain.getSingleInstance(appContext);
        this.contracts = controller.getStoreContracts();

        this.adapter = new ContractStoreFragment.ContractListAdapter(appContext, this.contracts);
        this.listView = (ListView) rootView.findViewById(R.id.listViewChooseContract);
        this.listView.setAdapter(this.adapter);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                StoreContract selected = contracts.get(position);


                Intent i = new Intent(ContractStoreFragment.this.getContext(), ContractStoreDetailActivity.class);
                i.putExtra(ContractStoreDetailActivity.APP_TO_VIEW, selected.getId().getId());
                startActivityForResult(i, MainActivity.TAB_TO_VIEW);

                // TODO: DO SOMETHING!
//                SwitchProfileActivity.this.setResult(RESULT_OK);
//                SwitchProfileActivity.this.finish();
            }
        });



        return rootView;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}