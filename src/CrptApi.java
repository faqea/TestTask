
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CrptApi {

    private TimeUnit timeWindow;
    private int requestLimit;
    private int requestCount;
    private volatile Instant windowStart;

    public static class Document {
        private Description description;
        private String docId;
        private String docStatus;
        private String docType;
        private boolean importRequest;
        private String ownerInn;
        private String participantInn;
        private String producerInn;
        private LocalDate productionDate;
        private String productionType;
        private Product product;
        private LocalDate regDate;
        private String regNumber;

        public Document() {

        }

        public Document(Description description, String docId, String docStatus, String docType, boolean importRequest, String ownerInn, String participantInn, String producerInn, LocalDate productionDate, String productionType, Product product, LocalDate regDate, String regNumber) {
            this.description = description;
            this.docId = docId;
            this.docStatus = docStatus;
            this.docType = docType;
            this.importRequest = importRequest;
            this.ownerInn = ownerInn;
            this.participantInn = participantInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.productionType = productionType;
            this.product = product;
            this.regDate = regDate;
            this.regNumber = regNumber;
        }

        public Description getDescription() {
            return description;
        }

        public String getDocId() {
            return docId;
        }

        public String getDocStatus() {
            return docStatus;
        }

        public String getDocType() {
            return docType;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public String getOwnerInn() {
            return ownerInn;
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public String getProducerInn() {
            return producerInn;
        }

        public LocalDate getProductionDate() {
            return productionDate;
        }

        public String getProductionType() {
            return productionType;
        }

        public Product getProduct() {
            return product;
        }

        public LocalDate getRegDate() {
            return regDate;
        }

        public String getRegNumber() {
            return regNumber;
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public void setDocStatus(String docStatus) {
            this.docStatus = docStatus;
        }

        public void setDocType(String docType) {
            this.docType = docType;
        }

        public void setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public void setOwnerInn(String ownerInn) {
            this.ownerInn = ownerInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }

        public void setProducerInn(String producerInn) {
            this.producerInn = producerInn;
        }

        public void setProductionDate(LocalDate productionDate) {
            this.productionDate = productionDate;
        }

        public void setProductionType(String productionType) {
            this.productionType = productionType;
        }

        public void setProduct(Product products) {
            this.product = product;
        }

        public void setRegDate(LocalDate regDate) {
            this.regDate = regDate;
        }

        public void setRegNumber(String regNumber) {
            this.regNumber = regNumber;
        }

        public static class Description {
            private String participantInn;
        }

        public static class Product {
            private String certificateDocument;
            private LocalDate certificateDocumentDate;
            private String certificateDocumentNumber;
            private String ownerInn;
            private String producerInn;
            private LocalDate productionDate;
            private String tnvedCode;
            private String uitCode;
            private String uituCode;
        }
    }

    public CrptApi(TimeUnit timeWindow, int requestLimit) {
        if (requestLimit <= 0) {
            return;
        }
        this.timeWindow = timeWindow;
        this.requestLimit = requestLimit;
        this.requestCount = 0;
        this.windowStart = Instant.now();
    }

    public synchronized boolean allowRequest() {
        Instant now = Instant.now();
        if (now.isAfter(windowStart.plus(timeWindow.toMillis(Long.MIN_VALUE), ChronoUnit.MILLIS))) {
            windowStart = now;
            requestCount = 0;
        }
        return requestCount <= requestLimit;
    }

    public Document createDoc(String doc, String certificate) {
        Document document = new Document();
        if (allowRequest()) {
            Document.Product product = new Document.Product();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                document = objectMapper.readValue(doc, Document.class);
                product = objectMapper.readValue(certificate, Document.Product.class);
                document.setProduct(product);
            } catch (IOException e) {
                System.out.println("Exception!");
            }
        }

        return document;
    }

    public TimeUnit getTimeWindow() {
        return timeWindow;
    }

    public int getRequestLimit() {
        return requestLimit;
    }
}
