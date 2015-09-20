package eip.com.lizz.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by guillaume on 28/03/15.
 */
public class UPhoneBook {

    public static String[] infosByPhone(ContentResolver cr, String contact)
    {
        String contactName = "", id = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact));
        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
        Cursor cursor =
                cr.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if(cursor!=null) {
            if(cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }
        String[] array = {contactName, getPhotoUri(cr, id)};
        return array;
    }

    public static String[] infosByEmail(ContentResolver cr, String contact)
    {
        String contactName = "", id = "";
        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.CONTACT_ID};
        Cursor emailCur = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                projection,
                ContactsContract.CommonDataKinds.Email.DATA
                        + " = '" + contact+"'", null, null);
        while (emailCur.moveToNext()) {
            contactName = emailCur.getString(emailCur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            id = emailCur.getString(emailCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
        }
        emailCur.close();
        String[] array = {contactName, getPhotoUri(cr, id)};
        return array;
    }

    public static String getPhotoUri(ContentResolver cr, String id) {
        try {
            Cursor cur = cr.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(id));
        return String.valueOf(Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY));
    }
}
