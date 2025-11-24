package es.clubdama;
import es.clubdama.dao.ReservaDao;
import es.clubdama.dao.JdbcUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaDaoTest {

    @Test
    public void testCrearReservaCallsStoredProcedure_success() throws Exception {
        // Mocks
        Connection mockConn = mock(Connection.class);
        CallableStatement mockCs = mock(CallableStatement.class);

        when(mockConn.prepareCall(anyString())).thenReturn(mockCs);

        // Mock static JdbcUtil.getConnection()
        try (MockedStatic<JdbcUtil> mocked = Mockito.mockStatic(JdbcUtil.class)) {
            mocked.when(JdbcUtil::getConnection).thenReturn(mockConn);

            ReservaDao dao = new ReservaDao();
            String id = dao.crearReserva("S-1","P-1", LocalDate.now(), LocalTime.of(10,0), 60);
            assertNotNull(id);

            // Verificar que el CallableStatement fue preparado y ejecutado
            verify(mockConn).prepareCall(anyString());
            verify(mockCs, atLeastOnce()).setString(anyInt(), anyString());
            verify(mockCs).setDate(anyInt(), any(Date.class));
            verify(mockCs).setTime(anyInt(), any(Time.class));
            verify(mockCs).setInt(anyInt(), anyInt());
            verify(mockCs).execute();
        }
    }

    @Test
    public void testCrearReservaProcedureThrowsSQLException() throws Exception {
        Connection mockConn = mock(Connection.class);
        CallableStatement mockCs = mock(CallableStatement.class);
        when(mockConn.prepareCall(anyString())).thenReturn(mockCs);
        doThrow(new SQLException("procedural error")).when(mockCs).execute();

        try (MockedStatic<JdbcUtil> mocked = Mockito.mockStatic(JdbcUtil.class)) {
            mocked.when(JdbcUtil::getConnection).thenReturn(mockConn);

            ReservaDao dao = new ReservaDao();
            Exception ex = assertThrows(Exception.class, () -> dao.crearReserva("S-1","P-1", LocalDate.now(), LocalTime.of(10,0), 60));
            assertTrue(ex.getMessage().toLowerCase().contains("error creando reserva") || ex.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testCalcularPrecioUsesFunction() throws Exception {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getDouble("precio")).thenReturn(12.5);

        try (MockedStatic<JdbcUtil> mocked = Mockito.mockStatic(JdbcUtil.class)) {
            mocked.when(JdbcUtil::getConnection).thenReturn(mockConn);

            ReservaDao dao = new ReservaDao();
            double precio = dao.calcularPrecio(45);
            assertEquals(12.5, precio, 0.0001);

            verify(mockConn).prepareStatement(contains("fn_precio_reserva"));
            verify(mockPs).setInt(1, 45);
            verify(mockPs).executeQuery();
        }
    }
}
