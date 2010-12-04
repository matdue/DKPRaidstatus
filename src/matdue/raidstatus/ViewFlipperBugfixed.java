package matdue.raidstatus;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * Bugfixed ViewFlipper class
 * 
 * Bug: http://code.google.com/p/android/issues/detail?id=6191
 *
 */
public class ViewFlipperBugfixed extends ViewFlipper {

	public ViewFlipperBugfixed(Context context) {
		super(context);
	}

	public ViewFlipperBugfixed(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		try {
            super.onDetachedFromWindow();
        } catch(Exception e) {
        	// Call stopFlipping() in order to kick off updateRunning()
			stopFlipping();
        }
	}

}
