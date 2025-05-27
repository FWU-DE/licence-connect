import logging
import sys
import os

logger = logging.getLogger("lc-halt")

stream_handler = logging.StreamHandler(sys.stdout)
log_formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(name)s: %(message)s")
stream_handler.setFormatter(log_formatter)
logger.addHandler(stream_handler)

logger.info("API is starting up")

log_level = os.getenv("LC_HALT_LOG_LEVEL", "INFO")
logger.info(f"Log level set to {log_level}")
logger.setLevel(log_level)
