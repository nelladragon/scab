package com.nelladragon.scab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public abstract class FactoringBaseFragment extends Fragment {
    Button conceedButton;
    TextView txtTargetComposite;
    TextView txtYourComposite;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_factoring, container, false);


        this.txtTargetComposite = (TextView) rootView.findViewById(R.id.textViewComposite);

        this.txtYourComposite = (TextView) rootView.findViewById(R.id.textViewYourComposite);
        this.txtYourComposite.setMovementMethod(new ScrollingMovementMethod());

        this.conceedButton = (Button) rootView.findViewById(R.id.buttonConcede);


        return rootView;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
