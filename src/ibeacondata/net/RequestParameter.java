package ibeacondata.net;

import java.io.Serializable;

/**
 * Created by LK on 2016/10/8.
 * 使用该类封装发送的参数
 */
public class RequestParameter implements Serializable,Comparable<Object>{

    private static final long serialVersionUID =1274906854152052510L;
    private String name;
    private String value;

    public RequestParameter(final String name,final String value){
        this.name= name;
        this.value = value;
    }
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public int compareTo(final Object another) {
        int compared;
        final RequestParameter parameter = (RequestParameter) another;
        compared = name.compareTo(parameter.name);
        if (compared == 0){
            compared = value.compareTo(parameter.value);
        }
        return compared;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o){
            return false;
        }
        if (this == o){
            return true;
        }
        if (o instanceof RequestParameter){
            final RequestParameter parameter = (RequestParameter) o;
            return name.equals(parameter.name) && value.equals(parameter.value);
        }
        return false;
    }
}
