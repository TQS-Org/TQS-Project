package TQS.project.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client user;

    @ManyToOne
    @JoinColumn(name = "charger_id")
    private Charger charger;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private int duration; // default to 20 minutes

    // Constructors
    public Booking() {
    }

    public Booking(Client user, Charger charger, LocalDateTime startTime, int duration) {
        this.user = user;
        this.charger = charger;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plusMinutes(this.duration);
        this.token = generateRandomToken(duration);
    }

    @PrePersist
    public void prePersist() {
        if (this.token == null || this.token.isEmpty()) {
            this.token = generateRandomToken(6);
        }

        // Calculate endTime if not set
        if (this.endTime == null && this.startTime != null) {
            this.endTime = this.startTime.plusMinutes(this.duration);
        }
    }

    private String generateRandomToken(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder token = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Client getUser() {
        return user;
    }

    public void setUser(Client user) {
        this.user = user;
    }

    public Charger getCharger() {
        return charger;
    }

    public void setCharger(Charger charger) {
        this.charger = charger;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(this.duration); // update endTime if startTime changes
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        if (this.startTime != null) {
            this.endTime = this.startTime.plusMinutes(duration); // update endTime if duration changes
        }
    }

    // Derived date from startTime
    public LocalDate getDate() {
        return startTime != null ? startTime.toLocalDate() : null;
    }
}
