package io.github.aerodlyn.atsl;

public class ATSLArray extends ATSLValue {
    public ATSLArray() {
        this(0);
    }

    public ATSLArray(int capacity) {
        value = new ATSLValue[capacity];
        type = TYPE.ARRAY;
    }

    @Override
    public ATSLValue add(ATSLValue value) {
        return null;
    }

    @Override
    public ATSLValue divide(ATSLValue value) {
        return null;
    }

    @Override
    public ATSLValue multiply(ATSLValue value) {
        return null;
    }

    @Override
    public ATSLValue subtract(ATSLValue value) {
        return null;
    }

    @Override
    public ATSLValue[] getValue() {
        return (ATSLValue[]) value;
    }

    public void putElement(int index, ATSLValue value) {
        int size = ((ATSLValue[]) this.value).length;

        if (index >= size)
            grow(index + 1);

        else if (index == -1) {
            grow(size + 1);
            index += size + 1;
        }

        ((ATSLValue[]) this.value)[index] = value;
    }

    private void grow(int capacity) {
        ATSLValue[] array = (ATSLValue[]) value;
        ATSLValue[] tmp = new ATSLValue[capacity];

        for (int i = 0; i < array.length; i++)
            tmp[i] = array[i];

        value = tmp;
    }

    public ATSLValue getElement(int index) {
        return ((ATSLValue[]) value)[index];
    }
}
