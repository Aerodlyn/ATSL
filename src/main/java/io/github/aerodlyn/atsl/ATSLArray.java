package io.github.aerodlyn.atsl;

public class ATSLArray extends ATSLValue {
    private TYPE containedType;

    public ATSLArray(TYPE type) {
        this(type, 0);
    }

    public ATSLArray(TYPE type, int capacity) {
        value = new ATSLValue[capacity];
        this.type = TYPE.ARRAY;
        
        containedType = type;
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
        if (containedType != TYPE.NONE && containedType != value.getType())
            throw new UnsupportedOperationException("Wrong type for Array");
        
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
        ATSLValue[] tmp = (ATSLValue[]) new ATSLValue[capacity];

        for (int i = 0; i < array.length; i++)
            tmp[i] = array[i];

        value = tmp;
    }

    public ATSLValue getElement(int index) {
        return ((ATSLValue[]) value)[index];
    }

    public TYPE getContainedType() {
        return containedType;
    }
}
