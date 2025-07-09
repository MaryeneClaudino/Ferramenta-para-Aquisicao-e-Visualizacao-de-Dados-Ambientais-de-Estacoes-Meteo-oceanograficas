package br.edu.ifrs.tcc.backend.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataKey {

    private Station station;
    private Parameter parameter;
    private Timestamp timestamp;

    public DataKey(Station station, Parameter parameter, Timestamp timestamp) {
        this.station = station;
        this.parameter = parameter;
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((station == null) ? 0 : station.hashCode());
        result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataKey other = (DataKey) obj;
        if (station == null) {
            if (other.station != null)
                return false;
        } else if (!station.equals(other.station))
            return false;
        if (parameter == null) {
            if (other.parameter != null)
                return false;
        } else if (!parameter.equals(other.parameter))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }
}