package org.example;

import org.example.data.Rec;

public interface ExtendHash {

    public void insert(Rec data);

    default String getBinaryStr(int num, int depth) {
        String binaryStr = Integer.toBinaryString(num);
        while (binaryStr.length() < depth) {
            binaryStr = "0" + binaryStr;
        }
        binaryStr = binaryStr.substring(binaryStr.length() - depth, binaryStr.length());
        return binaryStr;
    }
    public void delete(Rec data);

    public Boolean keyExists(Rec key);

    public Boolean search(Rec key);
}
