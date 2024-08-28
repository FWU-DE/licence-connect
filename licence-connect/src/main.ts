import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { VersioningType } from '@nestjs/common';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.enableVersioning({
    type: VersioningType.URI,
  });
  const config = new DocumentBuilder()
    .setTitle('Licence-connect API')
    .setDescription('This is the MVP Api for licence connect.')
    .setVersion('0.0.1')
    .addTag('licences')
    .addTag('licenceManagement')
    .addTag('ucs')
    .addApiKey(
      { type: 'apiKey', name: 'X-API-KEY', in: 'header' },
      'VIDIS-Core',
    )
    .addApiKey(
      { type: 'apiKey', name: 'X-API-KEY', in: 'header' },
      'LicenceManagement',
    )
    .addApiKey({ type: 'apiKey', name: 'X-API-KEY', in: 'header' }, 'ucs')
    .build();
  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('api', app, document);
  await app.listen(3000);
}
bootstrap();
