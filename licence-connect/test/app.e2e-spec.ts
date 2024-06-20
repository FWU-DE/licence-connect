import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from './../src/app.module';
import { incomingVidisCoreRequest } from '../src/domain/ucs/example-data';

describe('LicenceController (e2e)', () => {
  let app: INestApplication;

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  it('/licence (GET)', () => {
    return request(app.getHttpServer())
      .get('/licence')
      .send(incomingVidisCoreRequest)
      .expect(200)
      .expect('{"hasLicence":false,"licences":[]}');
  });
});
