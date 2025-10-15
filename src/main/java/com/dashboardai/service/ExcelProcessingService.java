package com.dashboardai.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelProcessingService.class);
    
    /**
     * Procesa un archivo Excel y extrae el contenido como texto estructurado
     */
    public String processExcelFile(MultipartFile file) throws IOException {
        logger.info("Processing Excel file: {} - Type: {}", file.getOriginalFilename(), file.getContentType());
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(file, inputStream);
            StringBuilder content = new StringBuilder();
            
            // Procesar cada hoja del libro
            int numberOfSheets = workbook.getNumberOfSheets();
            logger.info("Excel file has {} sheets", numberOfSheets);
            
            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                String sheetContent = processSheet(sheet);
                
                if (!sheetContent.trim().isEmpty()) {
                    content.append("=== HOJA: ").append(sheet.getSheetName()).append(" ===\n");
                    content.append(sheetContent);
                    content.append("\n\n");
                }
            }
            
            workbook.close();
            
            String result = content.toString().trim();
            logger.info("Excel processing completed. Content length: {} characters", result.length());
            
            return result;
        } catch (Exception e) {
            logger.error("Error processing Excel file: {}", e.getMessage(), e);
            throw new IOException("Error al procesar archivo Excel: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crea el workbook apropiado según el tipo de archivo
     */
    private Workbook createWorkbook(MultipartFile file, InputStream inputStream) throws IOException {
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        
        if (filename != null && filename.toLowerCase().endsWith(".xlsx") || 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
            return new XSSFWorkbook(inputStream);
        } else {
            return new HSSFWorkbook(inputStream);
        }
    }
    
    /**
     * Procesa una hoja individual del Excel
     */
    private String processSheet(Sheet sheet) {
        StringBuilder sheetContent = new StringBuilder();
        
        if (sheet.getPhysicalNumberOfRows() == 0) {
            return "";
        }
        
        // Determinar el rango de datos
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        
        logger.info("Processing sheet '{}' with {} rows (from {} to {})", 
                   sheet.getSheetName(), lastRowNum - firstRowNum + 1, firstRowNum, lastRowNum);
        
        // Detectar si la primera fila contiene encabezados
        Row firstRow = sheet.getRow(firstRowNum);
        List<String> headers = extractHeaders(firstRow);
        boolean hasHeaders = hasValidHeaders(headers);
        
        if (hasHeaders) {
            sheetContent.append("Columnas: ").append(String.join(", ", headers)).append("\n\n");
        }
        
        // Procesar las filas de datos
        int dataStartRow = hasHeaders ? firstRowNum + 1 : firstRowNum;
        int rowsProcessed = 0;
        
        for (int rowIndex = dataStartRow; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            
            String rowContent = processRow(row, headers, hasHeaders);
            if (!rowContent.trim().isEmpty()) {
                sheetContent.append(rowContent).append("\n");
                rowsProcessed++;
            }
            
            // Limitar el número de filas procesadas para evitar contenido demasiado largo
            if (rowsProcessed >= 1000) {
                sheetContent.append("... (limitado a 1000 filas para optimización)\n");
                break;
            }
        }
        
        logger.info("Processed {} data rows from sheet '{}'", rowsProcessed, sheet.getSheetName());
        return sheetContent.toString();
    }
    
    /**
     * Extrae los encabezados de la primera fila
     */
    private List<String> extractHeaders(Row row) {
        List<String> headers = new ArrayList<>();
        
        if (row == null) return headers;
        
        int lastCellNum = row.getLastCellNum();
        for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            String headerValue = getCellValueAsString(cell);
            headers.add(headerValue != null ? headerValue.trim() : "Columna" + (cellIndex + 1));
        }
        
        return headers;
    }
    
    /**
     * Determina si la fila parece contener encabezados válidos
     */
    private boolean hasValidHeaders(List<String> headers) {
        if (headers.isEmpty()) return false;
        
        long validHeaders = headers.stream()
                .filter(header -> header != null && !header.trim().isEmpty() && !header.matches("\\d+"))
                .count();
        
        // Si al menos el 50% de las columnas tienen texto que no son números, probablemente son encabezados
        return validHeaders >= headers.size() * 0.5;
    }
    
    /**
     * Procesa una fila individual
     */
    private String processRow(Row row, List<String> headers, boolean hasHeaders) {
        if (row == null) return "";
        
        StringBuilder rowContent = new StringBuilder();
        int lastCellNum = row.getLastCellNum();
        boolean hasContent = false;
        
        for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            String cellValue = getCellValueAsString(cell);
            
            if (cellValue != null && !cellValue.trim().isEmpty()) {
                if (hasContent) {
                    rowContent.append(" | ");
                }
                
                if (hasHeaders && cellIndex < headers.size()) {
                    rowContent.append(headers.get(cellIndex)).append(": ");
                }
                
                rowContent.append(cellValue.trim());
                hasContent = true;
            }
        }
        
        return hasContent ? rowContent.toString() : "";
    }
    
    /**
     * Convierte el valor de una celda a string
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        // Evitar notación científica para números
                        double numericValue = cell.getNumericCellValue();
                        if (numericValue == Math.floor(numericValue)) {
                            return String.valueOf((long) numericValue);
                        } else {
                            return String.valueOf(numericValue);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e) {
                        return cell.getStringCellValue();
                    }
                case BLANK:
                    return null;
                default:
                    return cell.toString();
            }
        } catch (Exception e) {
            logger.warn("Error reading cell value: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene estadísticas del archivo Excel procesado
     */
    public Map<String, Object> getExcelStats(MultipartFile file) throws IOException {
        Map<String, Object> stats = new HashMap<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(file, inputStream);
            
            stats.put("numberOfSheets", workbook.getNumberOfSheets());
            stats.put("fileSize", file.getSize());
            stats.put("fileName", file.getOriginalFilename());
            
            List<Map<String, Object>> sheetStats = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Map<String, Object> sheetInfo = new HashMap<>();
                sheetInfo.put("name", sheet.getSheetName());
                sheetInfo.put("rows", sheet.getLastRowNum() + 1);
                sheetInfo.put("physicalRows", sheet.getPhysicalNumberOfRows());
                sheetStats.add(sheetInfo);
            }
            stats.put("sheets", sheetStats);
            
            workbook.close();
        }
        
        return stats;
    }
}