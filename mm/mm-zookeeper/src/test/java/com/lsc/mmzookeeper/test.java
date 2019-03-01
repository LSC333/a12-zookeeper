package com.lsc.mmzookeeper;

import org.junit.Test;

/**
 * Created by ValarMorghulis on 2019/2/27 20:22.
 */
public class test {

    @Test
    public void qqq(){
        boolean flag=true;
        String str="/dasjdADAS/DASDda13asd123as";
        for(int i=0; i<str.length(); i++){
            char ch=str.charAt(i);
            if((ch<'a' || ch>'z')&&(ch<'A' || ch>'Z')&&(ch<'0' || ch>'9')&&(ch!='/')){
                flag=false;
                break;
            }
        }
        System.out.println(flag);
    }


}
