// Copyright (C) 2015 Peter Robinson
package com.nelladragon.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nelladragon.common.inapp.DonationCache;
import com.nelladragon.common.inapp.IabHelper;
import com.nelladragon.common.inapp.IabPubKey;
import com.nelladragon.common.inapp.IabResult;
import com.nelladragon.common.inapp.Inventory;
import com.nelladragon.common.inapp.Purchase;
import com.nelladragon.common.util.Fonts;

import java.util.List;

/**
 * Allows users to donate information.
 */
public class DonateActivity extends AppCompatActivity {
    private static final String TAG = DonateActivity.class.getSimpleName();

    public static final String DONATION_AMOUNT = "DONATION_AMOUNT";
    public static final int UNKNOWN_DEFAULT_AMOUNT = 1;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    /**
     * Adapter class to allow data to be transferred from the list and the activity.
     */
    private class DonationListAdapter extends BaseAdapter {
        private class ViewHolder {
            protected TextView desc;
            protected Button button;
        }

        private List<DonationCache.DonateItem> items;
        private LayoutInflater inflater;
        private Context context;

        public DonationListAdapter(Context context, List<DonationCache.DonateItem> items) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.items = items;
        }


        public int getCount() {
            return this.items.size();
        }

        // Get the item associated with the position in the dataset.
        public Object getItem(int position) {
            return this.items.get(position);
        }

        // Get the id associated with the position.
        public long getItemId(int position) {
            // There is no id for donations, so just return 0.
            return 0;

        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.list_item_donate, parent, false);

                holder = new ViewHolder();
                holder.desc = (TextView) convertView.findViewById(R.id.textViewDonateAmount);
                holder.button = (Button) convertView.findViewById(R.id.buttonDonateNow);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final DonationCache.DonateItem item = this.items.get(position);
            holder.desc.setText(item.description);
            holder.desc.setTypeface(font);
            if (item.purchased) {
                holder.button.setText(R.string.donate_thankyou1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.button.setBackgroundTintList(donateThankyouColorStateList);
                }
            } else {
                holder.button.setText(R.string.donate_donate);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.button.setBackgroundTintList(donatePleaseColorStateList);
                }
            }

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DonationCache.DonateItem item = (DonationCache.DonateItem) adapter.getItem(position);
                    if (item.purchased) {
                        // If already purchased, then show the "thank you" screen.
                        Log.d(TAG, "Show thank you: SKU: " + item.skuName);

                        Intent i = new Intent(DonateActivity.this, ThankyouActivity.class);
                        i.putExtra(DONATION_AMOUNT, item.amount);
                        startActivity(i);
                    } else {
                        // Launch purchase work flow.
                        Log.d(TAG, "Purchase button clicked; launching purchase flow: SKU: " + item.skuName);
                        updateStatus(true);

                    /* TODO: for security, generate your payload here for verification. See the comments on
                     *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
                     *        an empty string, but on a production app you should carefully generate this. */
                        String payload = "";

                        inAppPurchasingHelper.launchPurchaseFlow(DonateActivity.this, item.skuName, RC_REQUEST,
                                mPurchaseFinishedListener, payload);
                    }
                }
            });

            return convertView;
        }
    }


    ListView listView;
    DonationListAdapter adapter;


    // The helper object
    IabHelper inAppPurchasingHelper;

    ColorStateList donatePleaseColorStateList;
    ColorStateList donateThankyouColorStateList;

    Typeface font;
    TextView textViewStatus;

    DonationCache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.donatePleaseColorStateList = getResources().getColorStateList(R.color.donate_please);
        this.donateThankyouColorStateList = getResources().getColorStateList(R.color.donate_thankyou);
        this.font = Fonts.getChantelli(this);

        this.listView = (ListView) findViewById(R.id.listViewDonate);
        this.cache = DonationCache.getInstance(this);
        updateList();

        this.textViewStatus = (TextView) findViewById(R.id.textViewDonateStatus);

        String base64EncodedPublicKey = IabPubKey.construct();

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        inAppPurchasingHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        inAppPurchasingHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        inAppPurchasingHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (inAppPurchasingHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                inAppPurchasingHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });


    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (inAppPurchasingHelper != null) {
            inAppPurchasingHelper.dispose();
            inAppPurchasingHelper = null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (inAppPurchasingHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!inAppPurchasingHelper.handleActivityResult(requestCode, resultCode, data)) {
            // If this wasn't in-app purchasing related, then perhaps the
            // result is for the super class.
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }



    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (inAppPurchasingHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");
            cache.updateBasedOnInventory(inventory);
            updateList();

            updateStatus(false);
            Log.d(TAG, "Initial inventory query finished.");
        }
    };



    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (inAppPurchasingHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                updateStatus(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                updateStatus(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");
            cache.updateBasedOnPurchase(purchase);
            updateList();

            int amount = cache.getAmountBasedOnSku(purchase.getSku());

            Intent i = new Intent(DonateActivity.this, ThankyouActivity.class);
            i.putExtra(DONATION_AMOUNT, amount);
            startActivity(i);
            updateStatus(false);

        }
    };



    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * The following is from the Google example. Given the donations give
         * the user no real benefit, no anti-fraud measures seem to be warranted.
         *
         * Verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */
        return true;
    }


    // Update the list to reflect changes in the underlying data.
    public void updateList() {
        this.adapter = new DonationListAdapter(this, this.cache.loadDonationInfo());
        this.listView.setAdapter(this.adapter);
        this.listView.invalidate();
    }


    // Changes the status indicator.
    void updateStatus(boolean set) {
        String msg = set ? "Loading" : "Loaded";
        textViewStatus.setText(msg);
    }

    void complain(String message) {
        Log.e(TAG, "*Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            // Rather than trying to navigate to the "parent", just finish this activity, which will
            // return control to the "parent" activity.
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
