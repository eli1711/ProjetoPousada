package pousada;

import java.util.Date;

public class Reserva {
    private Date dataEntrada;
    private Date dataSaida;

    public Reserva(Date dataEntrada, Date dataSaida) {
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    // Verifica se esta reserva conflita com as datas fornecidas
    public boolean conflitaCom(Date checkIn, Date checkOut) {
        return !(checkOut.before(dataEntrada) || checkIn.after(dataSaida));
    }
}
