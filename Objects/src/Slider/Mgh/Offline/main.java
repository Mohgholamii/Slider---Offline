package Slider.Mgh.Offline;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "Slider.Mgh.Offline", "Slider.Mgh.Offline.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "Slider.Mgh.Offline", "Slider.Mgh.Offline.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Slider.Mgh.Offline.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public static anywheresoftware.b4a.objects.Timer _timer_slidehome = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel_fadehome = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _sliding_imagehome = null;
public static int _panel_alphahome = 0;
public static int _picture_positionhome = 0;
public anywheresoftware.b4a.objects.collections.List _list_picturehome = null;
public anywheresoftware.b4a.objects.LabelWrapper _llbhome = null;
public static int _i = 0;
public Slider.Mgh.Offline.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _plop3 = null;
anywheresoftware.b4a.objects.ImageViewWrapper _imgp3 = null;
anywheresoftware.b4a.objects.LabelWrapper _titelp3 = null;
anywheresoftware.b4a.objects.PanelWrapper _psliderhome = null;
anywheresoftware.b4a.objects.collections.List _list_filehome = null;
String _s = "";
 //BA.debugLineNum = 34;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 36;BA.debugLine="Dim plop3 As Panel";
_plop3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 37;BA.debugLine="plop3.Initialize(\"plop3\")";
_plop3.Initialize(mostCurrent.activityBA,"plop3");
 //BA.debugLineNum = 38;BA.debugLine="Activity.AddView(plop3,0%x,0%y,100%x,6.5%y)";
mostCurrent._activity.AddView((android.view.View)(_plop3.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (6.5),mostCurrent.activityBA));
 //BA.debugLineNum = 39;BA.debugLine="plop3.Color=Colors.White";
_plop3.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 40;BA.debugLine="plop3.Elevation=8dip";
_plop3.setElevation((float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (8))));
 //BA.debugLineNum = 42;BA.debugLine="Dim imgp3 As ImageView";
_imgp3 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 43;BA.debugLine="imgp3.Initialize(\"imgp3\")";
_imgp3.Initialize(mostCurrent.activityBA,"imgp3");
 //BA.debugLineNum = 44;BA.debugLine="plop3.AddView(imgp3,85%x,0.50%y,15%x,5.5%y)";
_plop3.AddView((android.view.View)(_imgp3.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (85),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (0.50),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (15),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5.5),mostCurrent.activityBA));
 //BA.debugLineNum = 45;BA.debugLine="imgp3.Gravity=Gravity.FILL";
_imgp3.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 50;BA.debugLine="Dim titelp3 As Label";
_titelp3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 51;BA.debugLine="titelp3.Initialize(\"titelp3\")";
_titelp3.Initialize(mostCurrent.activityBA,"titelp3");
 //BA.debugLineNum = 52;BA.debugLine="titelp3.Text=\"اسلایدر ساده\"";
_titelp3.setText(BA.ObjectToCharSequence("اسلایدر ساده"));
 //BA.debugLineNum = 53;BA.debugLine="titelp3.TextSize=20";
_titelp3.setTextSize((float) (20));
 //BA.debugLineNum = 54;BA.debugLine="titelp3.TextColor=0xFF5B5454";
_titelp3.setTextColor(((int)0xff5b5454));
 //BA.debugLineNum = 55;BA.debugLine="titelp3.Typeface=Typeface.LoadFromAssets(\"iransan";
_titelp3.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.LoadFromAssets("iransansmedium.ttf"));
 //BA.debugLineNum = 56;BA.debugLine="titelp3.Gravity=Gravity.CENTER";
_titelp3.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 57;BA.debugLine="plop3.AddView(titelp3,26%x,0.85%y,45%x,5.5%y)";
_plop3.AddView((android.view.View)(_titelp3.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (26),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (0.85),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (45),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5.5),mostCurrent.activityBA));
 //BA.debugLineNum = 60;BA.debugLine="Activity.Color=Colors.White";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 62;BA.debugLine="Dim psliderhome As Panel";
_psliderhome = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 63;BA.debugLine="psliderhome.Initialize(\"psliderhome\")";
_psliderhome.Initialize(mostCurrent.activityBA,"psliderhome");
 //BA.debugLineNum = 64;BA.debugLine="Activity.AddView(psliderhome,2.5%x,8%y,95.5%x,33%";
mostCurrent._activity.AddView((android.view.View)(_psliderhome.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2.5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (8),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (95.5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (33),mostCurrent.activityBA));
 //BA.debugLineNum = 70;BA.debugLine="sliding_imagehome.Initialize(\"sliding_imagehome\")";
mostCurrent._sliding_imagehome.Initialize(mostCurrent.activityBA,"sliding_imagehome");
 //BA.debugLineNum = 71;BA.debugLine="psliderhome.AddView(sliding_imagehome,2%x,1%y,92.";
_psliderhome.AddView((android.view.View)(mostCurrent._sliding_imagehome.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (92.5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (31),mostCurrent.activityBA));
 //BA.debugLineNum = 72;BA.debugLine="psliderhome.Elevation=2dip";
_psliderhome.setElevation((float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2))));
 //BA.debugLineNum = 75;BA.debugLine="llbhome.Initialize(\"llbhome\")";
mostCurrent._llbhome.Initialize(mostCurrent.activityBA,"llbhome");
 //BA.debugLineNum = 76;BA.debugLine="psliderhome.AddView(llbhome,2%x,26.2%y,92.1%x,5%y";
_psliderhome.AddView((android.view.View)(mostCurrent._llbhome.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (26.2),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (92.1),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 77;BA.debugLine="llbhome.Color=0x9AFFFFFF";
mostCurrent._llbhome.setColor(((int)0x9affffff));
 //BA.debugLineNum = 78;BA.debugLine="llbhome.Typeface=Typeface.LoadFromAssets(\"iransan";
mostCurrent._llbhome.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.LoadFromAssets("iransansmedium.ttf"));
 //BA.debugLineNum = 79;BA.debugLine="llbhome.Gravity=Gravity.CENTER";
mostCurrent._llbhome.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 80;BA.debugLine="llbhome.TextColor=Colors.Black";
mostCurrent._llbhome.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 81;BA.debugLine="llbhome.TextSize=22";
mostCurrent._llbhome.setTextSize((float) (22));
 //BA.debugLineNum = 82;BA.debugLine="panel_fadehome.Initialize(\"panel_fadehome\")";
mostCurrent._panel_fadehome.Initialize(mostCurrent.activityBA,"panel_fadehome");
 //BA.debugLineNum = 83;BA.debugLine="psliderhome.AddView(panel_fadehome, 1.5%x,1%y,93%";
_psliderhome.AddView((android.view.View)(mostCurrent._panel_fadehome.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (1.5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (93),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (31),mostCurrent.activityBA));
 //BA.debugLineNum = 85;BA.debugLine="Dim list_filehome As List = File.ListFiles(File.D";
_list_filehome = new anywheresoftware.b4a.objects.collections.List();
_list_filehome = anywheresoftware.b4a.keywords.Common.File.ListFiles(anywheresoftware.b4a.keywords.Common.File.getDirAssets());
 //BA.debugLineNum = 86;BA.debugLine="list_picturehome.Initialize";
mostCurrent._list_picturehome.Initialize();
 //BA.debugLineNum = 89;BA.debugLine="If list_filehome.Size > 0 Then";
if (_list_filehome.getSize()>0) { 
 //BA.debugLineNum = 90;BA.debugLine="For i = 0 To list_filehome.Size - 1";
{
final int step37 = 1;
final int limit37 = (int) (_list_filehome.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit37 ;_i = _i + step37 ) {
 //BA.debugLineNum = 91;BA.debugLine="Dim s As String = list_filehome.Get(i)";
_s = BA.ObjectToString(_list_filehome.Get(_i));
 //BA.debugLineNum = 92;BA.debugLine="If s.EndsWith(\".png\") = True And s.StartsWith(\"";
if (_s.endsWith(".png")==anywheresoftware.b4a.keywords.Common.True && _s.startsWith("slii_")==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 93;BA.debugLine="list_picturehome.Add(s)";
mostCurrent._list_picturehome.Add((Object)(_s));
 };
 }
};
 };
 //BA.debugLineNum = 100;BA.debugLine="timer_slidehome.Initialize(\"timer_slidehome\", 100";
_timer_slidehome.Initialize(processBA,"timer_slidehome",(long) (100));
 //BA.debugLineNum = 101;BA.debugLine="timer_slidehome.Enabled = True";
_timer_slidehome.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 110;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 106;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 114;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 115;BA.debugLine="xui.MsgboxAsync(\"Hello world!\", \"B4X\")";
_xui.MsgboxAsync(processBA,BA.ObjectToCharSequence("Hello world!"),BA.ObjectToCharSequence("B4X"));
 //BA.debugLineNum = 116;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 25;BA.debugLine="Dim panel_fadehome As Panel";
mostCurrent._panel_fadehome = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim sliding_imagehome As ImageView";
mostCurrent._sliding_imagehome = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim panel_alphahome As Int";
_panel_alphahome = 0;
 //BA.debugLineNum = 28;BA.debugLine="Dim picture_positionhome As Int";
_picture_positionhome = 0;
 //BA.debugLineNum = 29;BA.debugLine="Dim list_picturehome As List";
mostCurrent._list_picturehome = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 30;BA.debugLine="Dim llbhome As Label";
mostCurrent._llbhome = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim i=0 As Int";
_i = (int) (0);
 //BA.debugLineNum = 32;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Private xui As XUI";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 19;BA.debugLine="Dim timer_slidehome As Timer";
_timer_slidehome = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _sliding_imagehome_click() throws Exception{
 //BA.debugLineNum = 162;BA.debugLine="Sub sliding_imagehome_Click";
 //BA.debugLineNum = 164;BA.debugLine="ToastMessageShow(\"Slider Position Clicked\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Slider Position Clicked"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 166;BA.debugLine="End Sub";
return "";
}
public static String  _timer_slidehome_tick() throws Exception{
 //BA.debugLineNum = 118;BA.debugLine="Sub timer_slidehome_Tick";
 //BA.debugLineNum = 121;BA.debugLine="i=i+1";
_i = (int) (_i+1);
 //BA.debugLineNum = 122;BA.debugLine="panel_alphahome = panel_alphahome - 40";
_panel_alphahome = (int) (_panel_alphahome-40);
 //BA.debugLineNum = 123;BA.debugLine="If panel_alphahome < - 3500 Then";
if (_panel_alphahome<-3500) { 
 //BA.debugLineNum = 127;BA.debugLine="picture_positionhome = picture_positionhome + 1";
_picture_positionhome = (int) (_picture_positionhome+1);
 //BA.debugLineNum = 128;BA.debugLine="panel_alphahome = 255";
_panel_alphahome = (int) (255);
 //BA.debugLineNum = 129;BA.debugLine="panel_fadehome.Color = Colors.ARGB(panel_alphaho";
mostCurrent._panel_fadehome.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB(_panel_alphahome,(int) (255),(int) (255),(int) (255)));
 }else if(_panel_alphahome>0 && _panel_alphahome<256) { 
 //BA.debugLineNum = 131;BA.debugLine="panel_fadehome.Color = Colors.ARGB(panel_alphaho";
mostCurrent._panel_fadehome.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB(_panel_alphahome,(int) (255),(int) (255),(int) (255)));
 }else if(_panel_alphahome<0) { 
 //BA.debugLineNum = 133;BA.debugLine="panel_fadehome.Color = Colors.ARGB(0, 255, 255,";
mostCurrent._panel_fadehome.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (0),(int) (255),(int) (255),(int) (255)));
 };
 //BA.debugLineNum = 137;BA.debugLine="If picture_positionhome > list_picturehome.Size -";
if (_picture_positionhome>mostCurrent._list_picturehome.getSize()-1) { 
 //BA.debugLineNum = 138;BA.debugLine="picture_positionhome = 0";
_picture_positionhome = (int) (0);
 };
 //BA.debugLineNum = 141;BA.debugLine="sliding_imagehome.Bitmap = LoadBitmapSample(File.";
mostCurrent._sliding_imagehome.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapSample(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),BA.ObjectToString(mostCurrent._list_picturehome.Get(_picture_positionhome)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA)).getObject()));
 //BA.debugLineNum = 142;BA.debugLine="sliding_imagehome.Gravity=Gravity.FILL";
mostCurrent._sliding_imagehome.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 143;BA.debugLine="sliding_imagehome.Tag=i";
mostCurrent._sliding_imagehome.setTag((Object)(_i));
 //BA.debugLineNum = 144;BA.debugLine="If picture_positionhome==0 Then";
if (_picture_positionhome==0) { 
 //BA.debugLineNum = 145;BA.debugLine="llbhome.Text=\"کارتون الوین و سنجاب ها\"";
mostCurrent._llbhome.setText(BA.ObjectToCharSequence("کارتون الوین و سنجاب ها"));
 }else if(_picture_positionhome==1) { 
 //BA.debugLineNum = 147;BA.debugLine="llbhome.Text=\"برنامه کودک ولاد و نیکی\"";
mostCurrent._llbhome.setText(BA.ObjectToCharSequence("برنامه کودک ولاد و نیکی"));
 }else if(_picture_positionhome==2) { 
 //BA.debugLineNum = 149;BA.debugLine="llbhome.Text=\"دنیای بازی های آنلاین کودکانه\"";
mostCurrent._llbhome.setText(BA.ObjectToCharSequence("دنیای بازی های آنلاین کودکانه"));
 }else if(_picture_positionhome==3) { 
 //BA.debugLineNum = 151;BA.debugLine="llbhome.Text=\"دنیای آموزش های کودکانه\"";
mostCurrent._llbhome.setText(BA.ObjectToCharSequence("دنیای آموزش های کودکانه"));
 }else if(_picture_positionhome==4) { 
 //BA.debugLineNum = 153;BA.debugLine="llbhome.Text=\"برنامه کودک دیانا و روما\"";
mostCurrent._llbhome.setText(BA.ObjectToCharSequence("برنامه کودک دیانا و روما"));
 }else if(_picture_positionhome==5) { 
 //BA.debugLineNum = 155;BA.debugLine="llbhome.Text=\"کارتون جذاب و خنده دار باب اسفنجی\"";
mostCurrent._llbhome.setText(BA.ObjectToCharSequence("کارتون جذاب و خنده دار باب اسفنجی"));
 };
 //BA.debugLineNum = 161;BA.debugLine="End Sub";
return "";
}
}
