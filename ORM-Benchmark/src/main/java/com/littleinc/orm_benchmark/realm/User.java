package com.littleinc.orm_benchmark.realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


import static com.littleinc.orm_benchmark.util.Util.getRandomString;

/**
 * Created by kgalligan on 6/17/15.
 */
public class User extends RealmObject
{
    private int id;
    private String mLastName;
    private String mFirstName;

    public int getId()
    {
        return id;
    }

    public void setId(int mId)
    {
        this.id = mId;
    }

    public String getmLastName()
    {
        return mLastName;
    }

    public void setmLastName(String mLastName)
    {
        this.mLastName = mLastName;
    }

    public String getmFirstName()
    {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName)
    {
        this.mFirstName = mFirstName;
    }
}
