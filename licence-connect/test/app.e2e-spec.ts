import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from './../src/app.module';
import {
  incomingVidisCoreRequest,
  lcLicencesFromUCSResponse,
} from '../src/domain/ucs/example-data';

describe('LicenceController (e2e)', () => {
  let app: INestApplication;

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  it('/licence (POST)', () => {
    return request(app.getHttpServer())
      .get('/licences')
      .send(incomingVidisCoreRequest)
      .expect(200)
      .expect(
        `{"hasLicense":true,"licenses":${JSON.stringify(lcLicencesFromUCSResponse)}}`,
      );
  });
});
