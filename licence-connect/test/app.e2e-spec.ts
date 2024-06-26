import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from './../src/app.module';
import { RequestFromVidisCore } from '@domain/request-from-vidis-core';
import { ConfigurationService } from '@infrastructure/configuration/configuration.service';

describe('LicenceController (e2e)', () => {
  let app: INestApplication;

  const licenceManagerApiKey = 'licenceManager';
  const vidisApiKey = 'vidis';

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    })
      .overrideProvider(ConfigurationService)
      .useValue({
        getConfiguration: () => {
          return {
            vidisApiKey: vidisApiKey,
            licenceManagerApiKey: licenceManagerApiKey,
          };
        },
      })
      .compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  const requestWithNonExistingUser: RequestFromVidisCore = {
    userId: '00000',
    clientId: 'sodix-editor-o',
    schulkennung: 'DE-RP-SN-51201',
    bundesland: 'DE-MV',
  };

  describe('API KEY v1/licence (POST)', () => {
    describe('With valid api key', () => {
      it('in query', () => {
        return request(app.getHttpServer())
          .post(`/v1/licences/request?X-API-KEY=${vidisApiKey}`)
          .send(requestWithNonExistingUser)
          .expect(200)
          .expect(`{"hasLicence":false,"licences":[]}`);
      });

      it('in header', () => {
        return request(app.getHttpServer())
          .post('/v1/licences/request')
          .set({ 'X-API-KEY': vidisApiKey })
          .send(requestWithNonExistingUser)
          .expect(200)
          .expect(`{"hasLicence":false,"licences":[]}`);
      });
    });

    describe('With invalid api key', () => {
      it('in query', () => {
        return request(app.getHttpServer())
          .post(`/v1/licences/request?X-API-KEY=${vidisApiKey}wrongApiKey`)
          .send(requestWithNonExistingUser)
          .expect(403);
      });

      it('in header', () => {
        return request(app.getHttpServer())
          .post('/v1/licences/request')
          .set({ 'X-API-KEY': `${vidisApiKey}wrongApiKey` })
          .send(requestWithNonExistingUser)
          .expect(403);
      });
    });

    describe('Without api key', () => {
      it('in query', () => {
        return request(app.getHttpServer())
          .post('/v1/licences/request')
          .send(requestWithNonExistingUser)
          .expect(403);
      });
    });
  });

  describe('v1/licence/ (POST)', () => {
    it('Add licences', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/v1/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({
          studentId: 'Student1',
          licencesToAdd: [{ license_code: '1111' }],
        })
        .expect(201);
    });

    it('Reject licence request without licence_code', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/v1/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({ studentId: 'Student1', licencesToAdd: ['1111'] })
        .expect(400);
    });

    it('Reject licence request without studentId', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/v1/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({ licencesToAdd: [{ license_code: '1111' }] })
        .expect(400);
    });
  });

  describe('v1/licence/ (POST)', () => {
    it('Add and fetch licences', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/v1/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({
          studentId: 'Student1',
          licencesToAdd: [{ license_code: '1111' }, { license_code: '1112' }],
        })
        .then(() => {
          return request(app.getHttpServer())
            .post('/v1/licences/request')
            .set({ 'X-API-KEY': vidisApiKey })
            .send(licenceRequest)
            .expect(200)
            .expect(
              `{"hasLicence":true,"licences":[{"license_code":"1111"},{"license_code":"1112"}]}`,
            );
        })
        .then(() => {
          return request(app.getHttpServer())
            .delete('/v1/licences/remove')
            .set({ 'X-API-KEY': licenceManagerApiKey })
            .send({
              studentId: 'Student1',
            })
            .expect(200);
        })
        .then(() => {
          return request(app.getHttpServer())
            .post('/v1/licences/request')
            .set({ 'X-API-KEY': vidisApiKey })
            .send(licenceRequest)
            .expect(200)
            .expect(`{"hasLicence":false,"licences":[]}`);
        });
    });
  });
});
