package org.duckdns.altered.kellytv;

import android.content.Context;
import android.support.v17.leanback.widget.GuidedAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmorning on 2018-02-25.
 */

public class GuidedStepHelper {
    public static void addActionWithSub(List<GuidedAction> actions, Context context, long id,
                                         String title, String desc) {
        List<GuidedAction> subActions = new ArrayList();
        actions.add(new GuidedAction.Builder(context)
                .title(title)
                .id(id)
                .description(desc)
                .subActions(subActions)
                .build());
    }

    public static void addEditableAction(List<GuidedAction> actions, Context context, long id,
                                          String title, String desc) {
        actions.add(new GuidedAction.Builder(context)
                .id(id)
                .title(title)
                .description(desc).descriptionEditable(true)
                .build());
    }

    public static void addCheckedAction(List<GuidedAction> actions, Context context,
                                         String title, int id, boolean checked) {
        GuidedAction guidedAction = new GuidedAction.Builder(context)
                .title(title)
                .checkSetId(id)
                .build();
        guidedAction.setChecked(checked);
        actions.add(guidedAction);
    }

    public static void addAction(List<GuidedAction> actions, Context context, long id,
                                  String title, String desc) {
        actions.add(new GuidedAction.Builder(context)
                .id(id)
                .title(title)
                .description(desc).descriptionEditable(false)
                .build());
    }
}
