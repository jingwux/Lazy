package com.qingci.lazy.notify;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

/**
 * @author xieyuanqing -xieyuanqing@deepexi.com
 * @date 2020/7/11 -19:11
 */
public class FindConverterNotifier {
    private final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("Convert Find Error", NotificationDisplayType.BALLOON, true);

    public Notification notify(String content) {
        return notify(null, content);
    }

    public Notification warning(String content) {
        return notify(null, content, NotificationType.WARNING);
    }

    public Notification notify(Project project, String content) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION);
        notification.notify(project);
        return notification;
    }

    public Notification notify(Project project, String content, NotificationType type) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(content, type);
        notification.notify(project);
        return notification;
    }

}
