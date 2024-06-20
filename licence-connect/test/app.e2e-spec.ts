import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from './../src/app.module';
import {
  incomingVidisCoreRequest,
  lcLicencesFromUCSResponse,
} from '../src/domain/ucs/example-data';
import { ApiKeyService } from '../src/infrastructure/authentication/api-key.service';

describe('LicenceController (e2e)', () => {
  let app: INestApplication;

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    })
      .overrideProvider(ApiKeyService)
      .useValue({
        isApiKeyValid: (apiKey: string) => {
          return apiKey === 'test';
        },
      })
      .compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  describe('/licence (GET)', () => {
    describe('With valid api key', () => {
      it('in query', () => {
        return request(app.getHttpServer())
          .get('/licences?X-API-KEY=test')
          .send(incomingVidisCoreRequest)
          .expect(200)
          .expect(
            `{"hasLicence":true,"licences":${JSON.stringify(lcLicencesFromUCSResponse)}}`,
          );
      });

      it('in header', () => {
        return request(app.getHttpServer())
          .get('/licences')
          .set({ 'X-API-KEY': 'test' })
          .send(incomingVidisCoreRequest)
          .expect(200)
          .expect(
            `{"hasLicence":true,"licences":${JSON.stringify(lcLicencesFromUCSResponse)}}`,
          );
      });
    });

    describe('With invalid api key', () => {
      it('in query', () => {
        return request(app.getHttpServer())
          .get('/licences?X-API-KEY=wrongApiKey')
          .send(incomingVidisCoreRequest)
          .expect(403);
      });

      it('in header', () => {
        return request(app.getHttpServer())
          .get('/licences')
          .set({ 'X-API-KEY': 'wrongApiKey' })
          .send(incomingVidisCoreRequest)
          .expect(403);
      });
    });

    describe('Without api key', () => {
      it('in query', () => {
        return request(app.getHttpServer())
          .get('/licences')
          .send(incomingVidisCoreRequest)
          .expect(403);
      });
    });
  });
});
