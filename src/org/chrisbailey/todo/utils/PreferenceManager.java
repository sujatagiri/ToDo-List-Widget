/*******************************************************************************
 * ToDo List Widget - Android homescreen note taking application
 * Copyright (C) 2011  Chris Bailey
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.chrisbailey.todo.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import org.chrisbailey.todo.R;
import org.chrisbailey.todo.activities.ToDoActivity;
import org.chrisbailey.todo.db.ToDoDatabase;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

public class PreferenceManager 
{
    public static final String BACKGROUND_DRAWABLE_PREFIX = "background_";
    public static final String ACTIVE_DRAWABLE_PREFIX = "icon_active_";
    public static final String FINISHED_DRAWABLE_PREFIX = "icon_finished_";
    
    private static final String LOG_TAG = "ReferenceManager";

    private int currentBackground = -1;
    private int currentBackgroundRef = -1;
    private int currentIcon = -1;
    private int activeIconRef = -1;
    private int finishedIconRef = -1;
    private int currentActiveColor = -1;
    private int currentFinishedColor = -1;
    private int currentSize = -1;
    private boolean scrollButtons = true;
    private int defaultPadding = 1;
    private int topPadding = defaultPadding;
    
    public PreferenceManager(Context c, ToDoDatabase db)
    {
        setBackground(db.getPrefBackground());

        setIcons(db.getPrefIcon());
        
        int i = db.getPrefColorActive();
        if (i == 0) i = c.getResources().getColor(R.color.default_active_color);
        setActiveColor(i);
        
        i = db.getPrefColorFinished();
        if (i == 0) i = c.getResources().getColor(R.color.default_finished_color);
        setFinishedColor(i);
        
        i = db.getPrefSize();
        if (i == -1) i = (int) new TextView(c).getTextSize();
        setSize(i);
        
        scrollButtons = db.getScrollButtons();
    }
    
    /**
     * Save state to database
     * @param db
     */
    public void save(ToDoDatabase db)
    {
        if (ToDoActivity.debug) Log.i(LOG_TAG, "Saving: bg:" + currentBackground + " icon:" + currentIcon + " Acolor:"+currentActiveColor+" Fcolor:"+currentFinishedColor+" size:"+currentSize);
        db.setPrefBackground(currentBackground);
        db.setPrefColorActive(currentActiveColor);
        db.setPrefColorFinished(currentFinishedColor);
        db.setPrefIcon(currentIcon);
        db.setPrefSize(currentSize);
        db.setScrollButtons(scrollButtons);
    }
    
    public int getTopPadding()
    {
        return topPadding;
    }
    
    public void setSize(int i)
    {
        currentSize = i;
    }
    
    public int getSize()
    {
        return currentSize;
    }
    
    public int getTitleSize()
    {
        return currentSize+2;
    }
        
    public int getActiveColor()
    {
        return currentActiveColor;
    }
    
    public void setActiveColor(int i)
    {
        currentActiveColor = i;
    }
    
    public int getFinishedColor()
    {
        return currentFinishedColor;
    }
    
    public void setFinishedColor(int i)
    {
        currentFinishedColor = i;
    }
    
    public int[] getAllBackgrounds()
    {
        return getAllDrawables(BACKGROUND_DRAWABLE_PREFIX);
    }
    
    public int[] getAllIcons()
    {
        return getAllDrawables(ACTIVE_DRAWABLE_PREFIX);
    }
    
    private int[] getAllDrawables(String str)
    {
        ArrayList <Integer> drawables = new ArrayList<Integer>();

        try {
            Field f[] = R.drawable.class.getFields();

            for (int i = 0; i < f.length; i++)
            {
                if (f[i].getName().startsWith(str))
                {
                    String name = f[i].getName().replace(str, "");
                    if (name.contains("_")) name = name.replace(name.substring(name.lastIndexOf("_")),""); 
                    drawables.add(Integer.parseInt(name));
                }
            }
         }
         catch (Throwable e) {
            Log.e(LOG_TAG,"Error obtaining drawable",e);
         }
         
		try
		{
			Collections.sort(drawables);
		}
		catch (Exception e) { e.printStackTrace(); }
        
        int [] drawableReferences = new int[drawables.size()];
        int i = 0;
        for (Integer integer : drawables) drawableReferences[i++] = integer;
        
        return drawableReferences;
    }
    
    public void setBackground(int i)
    {
        if (i < 0) i = 1;
        currentBackground = i;
        currentBackgroundRef = getBackgroundRef(currentBackground);
    }
    public void setIcons(int i)
    {
        if (i < 0) i = 1;
        currentIcon = i;
        activeIconRef = getActiveIconRef(currentIcon);
        finishedIconRef = getFinishedIconRef(currentIcon);
    }

    /**
     * Returns the reference to the current background drawable
     * @return
     */
    public int getBackground()
    {
        return currentBackgroundRef;
    }
    
    /**
     * Returns the current background file number.
     * E.g. background2.png => 2
     * @return
     */
    public int getBackgroundId()
    {
        return currentBackground;
    }
    
    public int getIconId()
    {
        return currentIcon;
    }
    
    public int getActiveIcon()
    {
        return activeIconRef;
    }
    public int getFinishedIcon()
    {
        return finishedIconRef;
    }

    private int getActiveIconRef(int i)
    {
        return getDrawableField(i, ACTIVE_DRAWABLE_PREFIX);
    }

    private int getFinishedIconRef(int i)
    {
        return getDrawableField(i, FINISHED_DRAWABLE_PREFIX);
    }
    private int getBackgroundRef(int i)
    {
        return getDrawableField(i, BACKGROUND_DRAWABLE_PREFIX); 
    }
    
    public int getDrawableField(int i, String field)
    {
        try 
        {
        	if (field.equals(BACKGROUND_DRAWABLE_PREFIX)) field = field + i + "_";
        	else field = field + i;
        	
        	if (ToDoActivity.debug) Log.i(LOG_TAG,"Looking for " + field);

        	Field [] fields = R.drawable.class.getFields();
            for (Field f : fields)
            {
                if (f.getName().startsWith(field))
                {
                	if (ToDoActivity.debug) Log.i(LOG_TAG,"Getting field " + f.getName());
                    String name = f.getName().replace(field,"");
                    
                    if (name.length() > 0)
                    {
                        name = name.substring(name.lastIndexOf("_")+1); // remove leading _
                        topPadding = Integer.parseInt(name);
                    }
                    return f.getInt(null);
                }
            }
        } catch (Exception e)
        {
            Log.e(LOG_TAG,"Error obtaining drawable",e);
        }
        return -1;
    }
    
    public boolean isEmptyIcon()
    {
        return currentIcon == 9;
    }
    
    public boolean getScrollButtons()
    {
    	return scrollButtons;
    }
    
    public void setScrollButtons(boolean b)
    {
    	scrollButtons = b;
    }
}
