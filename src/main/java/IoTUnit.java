abstract class IoTUnit {

    private int id;

    public IoTUnit(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    abstract void copyInfoToThis(IoTUnit unit);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IoTUnit ioTUnit = (IoTUnit) o;

        return id == ioTUnit.id;
    }


    @Override
    public int hashCode() {
        return id;
    }
}
