package com.github.michqql.signcode.vault;

public class Code {

    private final int[] code;

    public Code() {
        this(4);
    }

    public Code(int length) {
        this.code = new int[length];
        for(int i = 0; i < length; i++)
            code[i] = -1;
    }

    public int getLength() {
        return code.length;
    }

    public int getNextFreeIndex() {
        for(int i = 0; i < code.length; i++) {
            if(code[i] == -1)
                return i;
        }

        return -1;
    }

    public int[] getCode() {
        return code;
    }

    public int getCodeAt(int index) {
        return code[index];
    }

    public void setCodeAt(int index, int c) {
        code[index] = c;
    }

    public void setCode(int[] chars) {
        if(chars.length != code.length)
            return;

        System.arraycopy(chars, 0, code, 0, code.length);
    }

    public boolean isComplete() {
        for(int c : code) {
            if(c == -1)
                return false;
        }

        return true;
    }

    public boolean equals(Code other) {
        for(int i = 0; i < code.length; i++) {
            if(code[i] != other.code[i])
                return false;
        }

        return true;
    }


}
