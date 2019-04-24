SUMMARY = "Grub configuration and other various files"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${RESIN_COREBASE}/COPYING.Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = " \
    file://grub.cfg_template \
    "

inherit deploy nopackages

INHIBIT_DEFAULT_DEPS = "1"

do_configure[noexec] = '1'
do_compile() {
    sed -e 's/@@KERNEL_CMDLINE@@/rootwait/' \
        "${WORKDIR}/grub.cfg_template" > "${B}/grub.cfg-dev"

    sed -e 's/@@KERNEL_CMDLINE@@/rootwait quiet loglevel=0 splash/' \
        "${WORKDIR}/grub.cfg_template" > "${B}/grub.cfg-prod"
}
do_install[noexec] = '1'
do_deploy() {
    if ${@bb.utils.contains('DISTRO_FEATURES','development-image','true','false',d)}; then
	install -m 644 -D ${B}/grub.cfg-dev ${DEPLOYDIR}/grub/grub.cfg
    else
	install -m 644 -D ${B}/grub.cfg-prod ${DEPLOYDIR}/grub/grub.cfg
    fi
}

addtask do_deploy before do_package after do_install
