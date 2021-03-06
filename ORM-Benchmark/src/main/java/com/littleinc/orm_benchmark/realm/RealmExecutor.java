package com.littleinc.orm_benchmark.realm;
import android.content.Context;
import android.util.Log;

import com.littleinc.orm_benchmark.BenchmarkExecutable;
import com.littleinc.orm_benchmark.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.littleinc.orm_benchmark.util.Util.getRandomString;

/**
 * Created by kgalligan on 6/17/15.
 */
public class RealmExecutor implements BenchmarkExecutable
{

    private static final String TAG = "RealmExecutor";

    private Context mContext;

    @Override
    public void init(Context context, boolean useInMemoryDb)
    {
        mContext = context;
        Realm.init(mContext);
    }

    @Override
    public String getOrmName()
    {
        return "Realm";
    }

    @Override
    public long createDbStructure() throws SQLException
    {
        return 0;
    }

    @Override
    public long writeWholeData() throws SQLException
    {
        List<User> users = new ArrayList<User>(NUM_USER_INSERTS);
        for (int i = 0; i < NUM_USER_INSERTS; i++) {
            User newUser = new User();
            newUser.setId(i);
            newUser.setmLastName(getRandomString(10));
            newUser.setmFirstName(getRandomString(10));

            users.add(newUser);
        }

        List<Message> messages = new ArrayList<Message>(NUM_MESSAGE_INSERTS);
        for (int i = 0; i < NUM_MESSAGE_INSERTS; i++) {
            Message newMessage = new Message();
            newMessage.setId(i);
            newMessage.setCommandId(i);
            newMessage.setSortedBy(System.nanoTime());
            newMessage.setContent(Util.getRandomString(100));
            newMessage.setClientId(System.currentTimeMillis());
            newMessage
                    .setSenderId(Math.round(Math.random() * NUM_USER_INSERTS));
            newMessage
                    .setChannelId(Math.round(Math.random() * NUM_USER_INSERTS));
            newMessage.setCreatedAt((int) (System.currentTimeMillis() / 1000L));


            messages.add(newMessage);
        }

        final Realm realm = Realm.getDefaultInstance();
        // Open the Realm for the UI thread.
        realm.beginTransaction();

        long start = System.nanoTime();

        String userLog;
        long messageStart;

        try
        {
            realm.insert(users);

            userLog = "Done, wrote " + NUM_USER_INSERTS + " users" + (System.nanoTime() - start);

            messageStart = System.nanoTime();

            realm.insert(messages);
        }
        finally
        {
            realm.commitTransaction();
            realm.close();
        }

        long totalTime = System.nanoTime() - start;

        Log.d(TAG, userLog);
        Log.d(TAG, "Done, wrote " + NUM_MESSAGE_INSERTS + " messages"  + (System.nanoTime() - messageStart));

        return totalTime;
    }

    @Override
    public long readWholeData() throws SQLException
    {
        final Realm realm = Realm.getDefaultInstance();
        long start = System.nanoTime();

        RealmQuery<User> userQuery = realm.where(User.class);
        RealmResults<User> userResults = userQuery.findAll();
        for(User user : userResults)
        {
            int id = user.getId();
            String first =  user.getmFirstName();
            String last = user.getmLastName();
        }

        String userLog = "Read " + userResults.size() + " users in " + (System.nanoTime() - start);

        long messageStart = System.nanoTime();

        RealmQuery <Message> messageQuery = realm.where(Message.class);
        RealmResults<Message> messageResults = messageQuery.findAll();
        for(Message message : messageResults)
        {
            int id = message.getId();
            long channel = message.getChannelId();
            long client = message.getClientId();
            long command = message.getCommandId();
            String content = message.getContent();
            int created = message.getCreatedAt();
            long sender = message.getSenderId();
            double sorted = message.getSortedBy();
        }

        long totalTime = System.nanoTime() - start;

        Log.d(TAG, userLog);
        Log.d(TAG,
              "Read " + messageResults.size() + " messages in " + (System.nanoTime() - messageStart));

        realm.close();

        return totalTime;
    }

    @Override
    public long dropDb() throws SQLException
    {
        long start = System.nanoTime();

        final Realm realm = Realm.getDefaultInstance();

        try
        {
            realm.beginTransaction();
            realm.deleteAll();
        }
        finally
        {
            realm.commitTransaction();
            realm.close();
        }

        return System.nanoTime() - start;
    }
}
