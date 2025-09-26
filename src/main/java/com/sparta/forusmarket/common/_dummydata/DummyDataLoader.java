package com.sparta.forusmarket.common._dummydata;

import com.sparta.forusmarket.common._dummydata.dto.ProductDto;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductGenerator productGenerator;
    private final BatchInserter batchInserter;

    @Value("${dummy.total_size}")
    private int TOTAL_PRODUCTS;

    @Value("${dummy.batch_size}")
    private int BATCH_SIZE;

    @Override
    public void run(String... args) {
        long count = productRepository.count();

        if (count > 0) {
            System.out.println("DB에 이미 상품 데이터가 있어서 더미 데이터 로드를 스킵합니다. (현재 개수: " + count + ")");
            return;
        }

        System.out.println("DB가 비어 있으므로 더미 데이터를 생성합니다...");

        long startTime = System.currentTimeMillis();

        List<ProductDto> buffers = new ArrayList<>(BATCH_SIZE);

        for (int i = 1; i <= TOTAL_PRODUCTS; i++) {
            buffers.add(productGenerator.generate(i));

            if (i % BATCH_SIZE == 0) {
                batchInserter.insertBatch(buffers);
                buffers.clear();
                System.out.println(i + " users inserted");
            }
        }

        if (!buffers.isEmpty()) {
            batchInserter.insertBatch(buffers);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total insert time: " + (endTime - startTime) / 1000 + " sec");
    }

}
