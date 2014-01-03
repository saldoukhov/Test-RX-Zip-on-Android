package saldoukhov.testrx1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import rx.Observable;
import rx.android.concurrency.AndroidSchedulers;
import rx.util.functions.Action1;
import rx.util.functions.Func2;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyActivity extends Activity {
    private Button testButton;
    private ImageView robotImg;
    private RelativeLayout.LayoutParams lp;
    private List<Pair<Integer, Integer>> path;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        testButton = (Button) findViewById(R.id.test_button);
        robotImg = (ImageView) findViewById(R.id.robot_img);
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        path = Arrays.asList(
                new Pair<Integer, Integer>(0, 0),
                new Pair<Integer, Integer>(0, 1),
                new Pair<Integer, Integer>(1, 1),
                new Pair<Integer, Integer>(1, 0),
                new Pair<Integer, Integer>(0, 0)
        );
        final Observable pathObservable = Observable.from(path);
        final Observable ticks = Observable.interval(500, TimeUnit.MILLISECONDS);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Observable robotMover = Observable
                        .zip(pathObservable, ticks, getZipFunction())
                        .observeOn(AndroidSchedulers.mainThread());
                robotMover.subscribe(new Action1<Pair<Integer, Integer>>() {
                    @Override
                    public void call(Pair<Integer, Integer> coordinates) {
                        lp.setMargins(300 * coordinates.first, 300 * coordinates.second, 0, 0);
                        robotImg.setLayoutParams(lp);
                        Log.d("RX", coordinates.toString());
                    }
                });
            }
        });
    }

    private Func2<Pair<Integer, Integer>, Long, Pair<Integer, Integer>> getZipFunction() {
        return new Func2<Pair<Integer, Integer>, Long, Pair<Integer, Integer>>() {
            @Override
            public Pair<Integer, Integer> call(Pair<Integer, Integer> coordinates, Long tick) {
                return coordinates;
            }
        };
    }
}
