package com.artificial;

public class DateKey {
    private final Date start, end;

    public DateKey(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof DateKey && start.equals(((DateKey) obj).getStart()) && end.equals(((DateKey) obj).getEnd());
    }
}
