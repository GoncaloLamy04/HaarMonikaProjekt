# Hårmoni'ka

Et tidsbestillingssystem til Monikas frisørsalon. Medarbejdere kan logge ind, oprette, redigere og aflyse aftaler.

## Opsætning

1. Klon projektet og åbn det i IntelliJ.
2. Kør `db-schema.sql` og derefter `db-seed.sql` i MySQL (begge ligger i `src/main/resources/`).
3. Opret en `db.properties` udfra `db.properties.example` i samme mappe og udfyld dine MySQL-oplysninger.
4. Kør `Launcher.java`.

Testbrugere kan ses i `db-seed.sql`.
