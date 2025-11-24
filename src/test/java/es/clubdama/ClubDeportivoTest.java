package es.clubdama;

import es.clubdama.dao.PistaDao;
import es.clubdama.dao.ReservaDao;
import es.clubdama.dao.SocioDao;
import es.clubdama.model.Reserva;
import es.clubdama.service.ClubDeportivo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClubDeportivoTest {

    @Test
    public void testBajaSocio_fallaSiTieneReservas() throws Exception {
        ReservaDao rDao = mock(ReservaDao.class);
        SocioDao sDao = mock(SocioDao.class);
        PistaDao pDao = mock(PistaDao.class);

        List<Reserva> reservas = new ArrayList<>();
        reservas.add(new Reserva());
        when(rDao.listarPorSocio("S1")).thenReturn(reservas);

        ClubDeportivo club = new ClubDeportivo(rDao, sDao, pDao);

        Exception ex = assertThrows(IllegalStateException.class, () -> club.bajaSocio("S1"));
        assertTrue(ex.getMessage().contains("reservas activas"));
    }

    @Test
    public void testBajaSocio_okSiNoTieneReservas() throws Exception {
        ReservaDao rDao = mock(ReservaDao.class);
        SocioDao sDao = mock(SocioDao.class);
        PistaDao pDao = mock(PistaDao.class);

        when(rDao.listarPorSocio("S2")).thenReturn(new ArrayList<>());

        ClubDeportivo club = new ClubDeportivo(rDao, sDao, pDao);

        club.bajaSocio("S2");
        verify(sDao).borrarPorId("S2");
    }
}

